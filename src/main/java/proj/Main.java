package proj;

import com.fasterxml.jackson.core.JsonProcessingException;

public class Main {
    private static final int Port = 1310;
    private static final int NumberOfClients = 20;
    public static void main(String[] args) throws JsonProcessingException {
        String dbFileName = "product";
        Connect conn = new Connect(dbFileName);
        ProductService ps = new ProductService(conn.getCon(), dbFileName);
        //ps.drop();
        Product p= new Product(113,"bread", 2,3, "dough", "tasty", "poroh");
        System.out.println(p);
        ps.insert(p);
        /*ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(p));
        p=mapper.readValue(mapper.writeValueAsString(p), Product.class);
        System.out.println(p);*/
        /*int id= ps.insert(p);
        p.setId(id);
        System.out.println(ps.read(id));
        p.setName("ChangedName");
        ps.update(p);
        System.out.println(ps.read(id));
        ps.delete(id);
        System.out.println(ps.read(id));
        System.out.println(ps.getAllProducts("name", "agg"));
        ps.drop();
        System.out.println(ps.getAllProducts());*/
    }
}