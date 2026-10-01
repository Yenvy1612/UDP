
package support;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.*;

/**
 * SOAP 1.1 / WSDL 1.1 document-literal wrapped, đọc schema khi chạy.
 * Hỗ trợ List biểu diễn bằng phần tử lặp hoặc một container có phần tử lặp.
 * Giữ bản XML đối tượng gốc; không tự bịa EmployeeY/Customer hoặc namespace.
 * Không hỗ trợ RPC/encoded, WS-Security, xsd:choice hoặc cấu trúc List tùy ý.
 */
public final class SoapCharacterArrayClient {
    private static final String W = "http://schemas.xmlsoap.org/wsdl/";
    private static final String X = "http://www.w3.org/2001/XMLSchema";
    private static final String S = "http://schemas.xmlsoap.org/wsdl/soap/";
    private static final String E = "http://schemas.xmlsoap.org/soap/envelope/";
    private static final int LIMIT = 4 * 1024 * 1024;
    private final URL source;
    private final Set<String> visited = new HashSet<>();
    private final List<Element> definitions = new ArrayList<>();
    private final List<Schema> schemas = new ArrayList<>();
    private String requestName;
    private final String submitName = "submitCharacterCharArray";
    private URL endpoint;
    private Element binding;
    private Operation requestOperation, submitOperation;
    private ListShape inputList, outputList;

    private static final class Schema {
        final Element element; final String namespace;
        Schema(Element element, String namespace) { this.element = element; this.namespace = namespace; }
    }
    private static final class Declaration {
        final Element element; final Schema schema;
        Declaration(Element element, Schema schema) { this.element = element; this.schema = schema; }
    }
    private static final class Field {
        final QName name; final Element element; final Schema schema;
        Field(QName name, Element element, Schema schema) {
            this.name = name; this.element = element; this.schema = schema;
        }
        boolean many() {
            String max = element.getAttribute("maxOccurs");
            return max.equals("unbounded") || (!max.isEmpty() && Integer.parseInt(max) > 1);
        }
    }
    private static final class Operation {
        QName wrapper, responseWrapper;
        String action;
        List<Field> parameters, results;
    }
    private static final class ListShape {
        final Field container, item;
        ListShape(Field container, Field item) { this.container = container; this.item = item; }
    }
    public static final class Item {
        private final Element element;
        private Item(Element element) { this.element = element; }
        public String text() {
            if (isNil(element)) throw new IllegalArgumentException("Phần tử List bị nil.");
            return element.getTextContent(); // Không trim hoặc bỏ chuỗi rỗng.
        }
        public String field(String name) {
            Element found = optional(element, null, name);
            if (found == null || isNil(found)) throw new IllegalArgumentException("Thiếu thuộc tính " + name);
            return found.getTextContent();
        }
        @Override public String toString() { return element.getTextContent(); }
    }

    public static Item scalar(String value) throws Exception {
        if (value == null) throw new IllegalArgumentException("Chuỗi null.");
        Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Element e = d.createElement("value"); e.setTextContent(value); d.appendChild(e);
        return new Item(e);
    }
    /**
     * Chỉ đọc metadata khi chọn operation, chưa gọi request/submit.
     * Ưu tiên tên trong đề. Chỉ dùng requestCharacterArray nếu WSDL thật
     * có operation đó với hai tham số và response List phù hợp.
     */
    public SoapCharacterArrayClient(String wsdlUrl) throws Exception {
        source = new URL(wsdlUrl);
        load(source, "", 0);
        List<String> issues = new ArrayList<>();
        Exception lastError = null;
        for (String candidate : new String[]{"requestCharacter", "requestCharacterArray"}) {
            requestName = candidate;
            binding = null;
            endpoint = null;
            try {
                selectBinding();
                requestOperation = operation(requestName, 2);
                submitOperation = operation(submitName, 3);
                if (requestOperation.results.size() != 1) {
                    throw new IOException("Response không có đúng một trường List.");
                }
                outputList = listShape(requestOperation.results.get(0));
                inputList = listShape(submitOperation.parameters.get(2));
                return;
            } catch (IOException e) {
                lastError = e;
                issues.add(candidate + ": " + e.getMessage());
            }
        }
        throw new IOException("Không có phương thức nhận mảng ký tự phù hợp trong WSDL. "
                + String.join(" | ", issues), lastError);
    }

    public String requestMethod() { return requestName; }
    public String endpointAddress() { return endpoint.toExternalForm(); }

    public List<Item> request(String studentCode, String qCode) throws Exception {
        Document d = message(requestOperation);
        Element op = required(required(d.getDocumentElement(), E, "Body"),
                requestOperation.wrapper.getNamespaceURI(), requestOperation.wrapper.getLocalPart());
        appendText(d, op, requestOperation.parameters.get(0).name, studentCode);
        appendText(d, op, requestOperation.parameters.get(1).name, qCode);
        Element response = call(requestOperation, d);
        Element parent = response;
        if (outputList.container != null) {
            QName q = outputList.container.name;
            parent = optional(response, q.getNamespaceURI(), q.getLocalPart());
            if (parent == null || isNil(parent)) return new ArrayList<>();
        }
        QName itemName = outputList.item.name;
        List<Item> items = new ArrayList<>();
        for (Element e : children(parent, itemName.getNamespaceURI(), itemName.getLocalPart())) {
            items.add(new Item(e));
        }
        return items;
    }

    public void submit(String studentCode, String qCode, List<Item> values) throws Exception {
        Objects.requireNonNull(values, "List null");
        Document d = message(submitOperation);
        Element op = required(required(d.getDocumentElement(), E, "Body"),
                submitOperation.wrapper.getNamespaceURI(), submitOperation.wrapper.getLocalPart());
        appendText(d, op, submitOperation.parameters.get(0).name, studentCode);
        appendText(d, op, submitOperation.parameters.get(1).name, qCode);
        Element parent = op;
        if (inputList.container != null) {
            parent = create(d, inputList.container.name);
            op.appendChild(parent);
        }
        for (Item item : values) {
            if (item == null) throw new IllegalArgumentException("List chứa phần tử null.");
            parent.appendChild(copyAs(d, inputList.item.name, item.element));
        }
        call(submitOperation, d);
    }

    /** xsd:unsignedShort/int thường là biểu diễn SOAP của Java char. */
    public int firstCharacterCode(Item item) throws Exception {
        QName type = typeName(outputList.item);
        String value = item.text();
        if (type != null && type.getNamespaceURI().equals(X)
                && Arrays.asList("unsignedShort", "int", "short", "integer", "unsignedByte")
                    .contains(type.getLocalPart())) {
            int n = Integer.parseInt(value.trim());
            if (n < 0 || n > Character.MAX_CODE_POINT) throw new IllegalArgumentException("Mã ký tự không hợp lệ.");
            return n;
        }
        if (value.isEmpty()) throw new IllegalArgumentException("Phần tử ký tự đầu rỗng.");
        if (value.codePointCount(0, value.length()) != 1) {
            throw new IllegalArgumentException("Phần tử không phải một ký tự; cần kiểm tra schema: " + value);
        }
        return value.codePointAt(0);
    }

    private static boolean isNil(Element e) {
        String nil = e.getAttributeNS(XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI, "nil");
        return nil.equals("true") || nil.equals("1");
    }
    private ListShape listShape(Field f) throws Exception {
        if (f.many()) return new ListShape(null, f);
        List<Field> fields = fields(f, 0);
        if (fields.size() == 1 && fields.get(0).many()) return new ListShape(f, fields.get(0));
        throw new IOException("List cần maxOccurs>1 hoặc container có một trường lặp: " + f.name);
    }
    private Field globalField(QName name) throws Exception {
        Declaration d = schemaDeclaration("element", name);
        return new Field(name, d.element, d.schema);
    }
    private QName typeName(Field f) throws Exception {
        if (!f.element.getAttribute("ref").isEmpty()) f = globalField(qname(f.element, "ref"));
        if (f.element.getAttribute("type").isEmpty()) return null;
        QName type = qname(f.element, "type");
        if (!type.getNamespaceURI().equals(X)) {
            try {
                Declaration d = schemaDeclaration("simpleType", type);
                Element restriction = optional(d.element, X, "restriction");
                if (restriction != null) return qname(restriction, "base");
            } catch (IOException ignored) { /* complex type */ }
        }
        return type;
    }
    private List<Field> fields(Field f, int depth) throws Exception {
        if (depth > 30) throw new IOException("Schema quá sâu.");
        if (!f.element.getAttribute("ref").isEmpty()) return fields(globalField(qname(f.element, "ref")), depth+1);
        Element complex = optional(f.element, X, "complexType");
        Schema schema = f.schema;
        if (complex == null) {
            if (f.element.getAttribute("type").isEmpty()) return new ArrayList<>();
            QName type = qname(f.element, "type");
            if (type.getNamespaceURI().equals(X)) return new ArrayList<>();
            Declaration d;
            try { d = schemaDeclaration("complexType", type); }
            catch (IOException ex) { return new ArrayList<>(); }
            complex = d.element; schema = d.schema;
        }
        List<Field> result = new ArrayList<>();
        Element sequence = optional(complex, X, "sequence");
        if (sequence == null) {
            if (children(complex, X, null).isEmpty()) return result; // void response.
            throw new IOException("Chỉ hỗ trợ complexType/sequence trong wrapper/List.");
        }
        for (Element e : children(sequence, X, "element")) {
            QName name;
            if (!e.getAttribute("ref").isEmpty()) name = qname(e, "ref");
            else {
                String form = e.getAttribute("form");
                if (form.isEmpty()) form = schema.element.getAttribute("elementFormDefault");
                name = new QName(form.equals("qualified") ? schema.namespace : "", e.getAttribute("name"));
            }
            result.add(new Field(name, e, schema));
        }
        return result;
    }

    private void selectBinding() throws Exception {
        for (Element d : definitions) for (Element service : children(d, W, "service")) {
            for (Element port : children(service, W, "port")) {
                Element address = optional(port, S, "address");
                if (address == null) continue;
                Element candidate = definition("binding", qname(port, "binding"));
                Element soapBinding = optional(candidate, S, "binding");
                if (soapBinding == null) continue;
                String style = soapBinding.getAttribute("style");
                if (!style.isEmpty() && !style.equals("document")) continue;
                if (named(candidate, W, "operation", requestName) == null
                        || named(candidate, W, "operation", submitName) == null) continue;
                binding = candidate;
                URL declared = resolve(source, address.getAttribute("location"));
                endpoint = new URL(source.getProtocol(), source.getHost(), source.getPort(), declared.getFile());
                checkHttp(endpoint); return;
            }
        }
        throw new IOException("WSDL không có SOAP 1.1 document binding cho " + requestName + " / " + submitName);
    }
    private Operation operation(String name, int count) throws Exception {
        Element bound = named(binding, W, "operation", name);
        if (bound == null) throw new IOException("WSDL thiếu operation " + name);
        Element soap = required(bound, S, "operation");
        String style = soap.getAttribute("style");
        if (!style.isEmpty() && !style.equals("document")) throw new IOException("Cần document/literal.");
        Element body = required(required(bound, W, "input"), S, "body");
        if (!body.getAttribute("use").equals("literal")) throw new IOException("Cần SOAP literal.");
        Element pt = definition("portType", qname(binding, "type"));
        Element operation = named(pt, W, "operation", name);
        if (operation == null) throw new IOException("Không có portType operation " + name);
        Operation out = new Operation();
        out.wrapper = wrapper(required(operation, W, "input"));
        out.responseWrapper = wrapper(required(operation, W, "output"));
        out.action = soap.getAttribute("soapAction");
        out.parameters = fields(globalField(out.wrapper), 0);
        out.results = fields(globalField(out.responseWrapper), 0);
        if (out.parameters.size() != count) throw new IOException(name + ": sai số tham số trong WSDL.");
        return out;
    }
    private QName wrapper(Element inOrOut) throws Exception {
        Element message = definition("message", qname(inOrOut, "message"));
        List<Element> parts = children(message, W, "part");
        if (parts.size() != 1 || parts.get(0).getAttribute("element").isEmpty()) {
            throw new IOException("Cần SOAP document/literal wrapped.");
        }
        return qname(parts.get(0), "element");
    }
    private static Element create(Document d, QName q) {
        Element e = d.createElementNS(q.getNamespaceURI().isEmpty() ? null : q.getNamespaceURI(), q.getLocalPart());
        e.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", q.getNamespaceURI());
        return e;
    }
    private static void appendText(Document d, Element p, QName q, String text) {
        if (text == null) throw new IllegalArgumentException("Tham số null.");
        Element e = create(d, q); e.setTextContent(text); p.appendChild(e);
    }
    private static Element copyAs(Document d, QName q, Element source) {
        Element e = create(d, q);
        // Bảo toàn khai báo prefix, kể cả prefix trong thuộc tính xsi:type.
        List<Element> ancestors = new ArrayList<>();
        for (Node n = source; n instanceof Element; n = n.getParentNode()) ancestors.add((Element)n);
        Collections.reverse(ancestors);
        for (Element ancestor : ancestors) {
            NamedNodeMap attrs = ancestor.getAttributes();
            for (int i = 0; i < attrs.getLength(); i++) {
                Attr a = (Attr) attrs.item(i);
                if (XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())) {
                    e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
                }
            }
        }
        NamedNodeMap attrs = source.getAttributes();
        for (int i = 0; i < attrs.getLength(); i++) {
            Attr a = (Attr)attrs.item(i);
            if (!XMLConstants.XMLNS_ATTRIBUTE_NS_URI.equals(a.getNamespaceURI())) {
                e.setAttributeNS(a.getNamespaceURI(), a.getName(), a.getValue());
            }
        }
        e.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns", q.getNamespaceURI());
        for (Node n = source.getFirstChild(); n != null; n = n.getNextSibling()) {
            e.appendChild(d.importNode(n, true));
        }
        return e;
    }
    private static Document message(Operation op) throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance(); f.setNamespaceAware(true);
        Document d = f.newDocumentBuilder().newDocument();
        Element envelope = d.createElementNS(E, "soap:Envelope"); d.appendChild(envelope);
        envelope.setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:soap", E);
        Element body = d.createElementNS(E, "soap:Body"); envelope.appendChild(body);
        body.appendChild(create(d, op.wrapper)); return d;
    }
    private Element call(Operation op, Document message) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        tf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer t = tf.newTransformer(); t.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        t.transform(new DOMSource(message), new StreamResult(out));
        Document response = parse(http(endpoint, out.toByteArray(), op.action));
        Element envelope = response.getDocumentElement();
        if (!matches(envelope, E, "Envelope")) throw new IOException("Không phải SOAP 1.1 Envelope.");
        Element body = required(envelope, E, "Body");
        Element fault = optional(body, E, "Fault");
        if (fault != null) throw new IOException("SOAP Fault: " + fault.getTextContent());
        return required(body, op.responseWrapper.getNamespaceURI(), op.responseWrapper.getLocalPart());
    }
    private void load(URL url, String inheritedNamespace, int depth) throws Exception {
        checkHttp(url);
        if (depth > 12) throw new IOException("WSDL/XSD import qua sau.");
        if (!visited.add(url.toExternalForm() + "|" + inheritedNamespace)) return;
        if (visited.size() > 40) throw new IOException("Qua nhieu tep WSDL/XSD.");
        Element root = parse(http(url, null, null)).getDocumentElement();
        if (matches(root, W, "definitions")) {
            definitions.add(root);
            for (Element imported : children(root, W, "import")) {
                if (!imported.getAttribute("location").isEmpty()) {
                    load(resolve(url, imported.getAttribute("location")), "", depth + 1);
                }
            }
            for (Element types : children(root, W, "types")) {
                for (Element schema : children(types, X, "schema")) {
                    addSchema(schema, url, "", depth);
                }
            }
        } else if (matches(root, X, "schema")) {
            addSchema(root, url, inheritedNamespace, depth);
        } else {
            throw new IOException("URL khong tra WSDL/XSD: " + url);
        }
    }

    private void addSchema(Element root, URL base, String inherited, int depth)
            throws Exception {
        String ns = root.getAttribute("targetNamespace");
        if (ns.isEmpty()) ns = inherited;
        schemas.add(new Schema(root, ns));
        for (Element child : children(root, X, null)) {
            String local = child.getLocalName();
            if (!local.equals("import") && !local.equals("include")) continue;
            String location = child.getAttribute("schemaLocation");
            if (location.isEmpty()) continue;
            String inheritedNs = local.equals("include") ? ns : "";
            load(resolve(base, location), inheritedNs, depth + 1);
        }
    }

    private Element definition(String kind, QName name) throws IOException {
        for (Element root : definitions) {
            if (root.getAttribute("targetNamespace").equals(name.getNamespaceURI())) {
                Element found = named(root, W, kind, name.getLocalPart());
                if (found != null) return found;
            }
        }
        throw new IOException("Khong tim thay wsdl:" + kind + " " + name);
    }

    private Declaration schemaDeclaration(String kind, QName name) throws IOException {
        for (Schema schema : schemas) {
            if (schema.namespace.equals(name.getNamespaceURI())) {
                Element found = named(schema.element, X, kind, name.getLocalPart());
                if (found != null) return new Declaration(found, schema);
            }
        }
        throw new IOException("Khong tim thay xsd:" + kind + " " + name);
    }

    private static QName qname(Element element, String attribute) throws IOException {
        String text = element.getAttribute(attribute);
        if (text.isEmpty()) throw new IOException("Thieu thuoc tinh " + attribute);
        int colon = text.indexOf(':');
        String prefix = colon < 0 ? null : text.substring(0, colon);
        String ns = element.lookupNamespaceURI(prefix);
        if (prefix != null && ns == null) throw new IOException("Prefix khong ro: " + text);
        return new QName(ns == null ? "" : ns, text.substring(colon + 1));
    }

    private static List<Element> children(Element root, String ns, String local) {
        List<Element> result = new ArrayList<>();
        for (Node n = root.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element && matches((Element) n, ns, local)) {
                result.add((Element) n);
            }
        }
        return result;
    }

    private static boolean matches(Element e, String ns, String local) {
        String namespace = e.getNamespaceURI() == null ? "" : e.getNamespaceURI();
        return (ns == null || ns.equals(namespace))
                && (local == null || local.equals(e.getLocalName()));
    }

    private static Element optional(Element e, String ns, String local) {
        List<Element> items = children(e, ns, local);
        return items.isEmpty() ? null : items.get(0);
    }

    private static Element required(Element e, String ns, String local) throws IOException {
        Element found = optional(e, ns, local);
        if (found == null) throw new IOException("Thieu XML element {" + ns + "}" + local);
        return found;
    }

    private static Element named(Element e, String ns, String local, String name) {
        for (Element child : children(e, ns, local)) {
            if (child.getAttribute("name").equals(name)) return child;
        }
        return null;
    }

    private static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;")
                .replace(">", "&gt;").replace("\"", "&quot;").replace("'", "&apos;");
    }

    private static Document parse(byte[] bytes) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);
        return factory.newDocumentBuilder().parse(new ByteArrayInputStream(bytes));
    }

    private static URL resolve(URL base, String location) throws IOException {
        // java.net.URL resolves a query-only reference to the parent path;
        // keep the current service path for the common ?xsd=1 reference.
        if (location.startsWith("?")) {
            return new URL(base.getProtocol(), base.getHost(), base.getPort(),
                    base.getPath() + location);
        }
        return new URL(base, location);
    }

    private static void checkHttp(URL url) throws IOException {
        if (!url.getProtocol().equals("http") && !url.getProtocol().equals("https")) {
            throw new IOException("Chi cho phep HTTP(S): " + url);
        }
    }

    private static byte[] http(URL url, byte[] payload, String action) throws IOException {
        checkHttp(url);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        try {
            if (payload != null) {
                if (action.contains("\r") || action.contains("\n") || action.contains("\"")) {
                    throw new IOException("SOAPAction khong hop le.");
                }
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "text/xml; charset=UTF-8");
                connection.setRequestProperty("SOAPAction", "\"" + action + "\"");
                connection.setFixedLengthStreamingMode(payload.length);
                try (OutputStream out = connection.getOutputStream()) {
                    out.write(payload);
                }
            }
            int status = connection.getResponseCode();
            InputStream stream = status >= 400
                    ? connection.getErrorStream() : connection.getInputStream();
            if (stream == null) throw new IOException("HTTP " + status + " tai " + url);
            byte[] data;
            try (InputStream in = stream; ByteArrayOutputStream out = new ByteArrayOutputStream()) {
                byte[] buffer = new byte[4096];
                int n;
                while ((n = in.read(buffer)) != -1) {
                    if (out.size() + n > LIMIT) throw new IOException("Phan hoi HTTP qua lon.");
                    out.write(buffer, 0, n);
                }
                data = out.toByteArray();
            }
            if (status < 200 || status >= 300) {
                String message = new String(data, StandardCharsets.UTF_8);
                throw new IOException("HTTP " + status + " tai " + url + ": "
                        + message.substring(0, Math.min(message.length(), 1800)));
            }
            return data;
        } finally {
            connection.disconnect();
        }
    }
}
