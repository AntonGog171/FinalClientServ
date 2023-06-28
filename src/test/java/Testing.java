import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import proj.Connect;
import proj.ProductService;
import proj.Server;
import org.junit.Assert;
import org.junit.Test;

import static proj.Server.adminLOGIN;
import static proj.Server.adminPASSWORD;

public class Testing {
    private static String token ="";



    //я без поняття чому тут якісь ексепшени
    // (які ще й нічого не роблять і випадають тільки на хороших запитах),
    // адже це просто коди з postman скопійовані,
    // коли напряму там робити запити- ніяких ексепшинів немає.
    // Дивно короче, але воно бездоганно працює



    @Test
    public void test() throws Exception {
        String dbFileName = "test_db";
        Connect conn = new Connect(dbFileName, Server.category_db);
        ProductService ps = new ProductService(conn.getCon(), dbFileName);
        ps.drop();

        Server server = new Server();
        Server.run();
        HttpResponse<String> response = Unirest.post("http://localhost:1310/login")
                .header("Content-Type", "application/json")
                .body("{\"login\":\""+adminLOGIN+"\", \"password\":\""+adminPASSWORD+"\"}")
                .asString();
        System.out.println("Testing good login: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(200, response.getStatus());
        token=response.getBody().substring(3);
        response = Unirest.post("http://localhost:1310/login")
                .header("Content-Type", "application/json")
                .body("{\"login\":\""+adminLOGIN+"2\", \"password\":\""+adminPASSWORD+"\"}")
                .asString();
        System.out.println("Testing bad login: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(401, response.getStatus());


        response = Unirest.put("http://localhost:1310/api/good")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .body("{\"id\":1,\"name\":\"2\",\"price\": 3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good PUT: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(201, response.getStatus());

        response = Unirest.put("http://localhost:1310/api/good")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer f"+token.substring(1))
                .body("{\"id\":1,\"name\":\"2\",\"price\": 3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good PUT with wrong token: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(401, response.getStatus());

        response = Unirest.put("http://localhost:1310/api/good")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer "+token)
                .body("{\"id\":1,\"name\":\"2\",\"price\": 3.0,\"quantity\":-4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good PUT with wrong token: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(409, response.getStatus());

        response = Unirest.get("http://localhost:1310/api/good/1")
                .header("Authorization", "Bearer "+token)
                .asString();
        System.out.println("Testing good GET : "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(200, response.getStatus());

        response = Unirest.get("http://localhost:1310/api/good/1")
                .header("Authorization", "Bearer f"+token.substring(1))
                .asString();
        System.out.println("Testing good GET with wrong token: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(401, response.getStatus());

        response = Unirest.get("http://localhost:1310/api/good/10000")
                .header("Authorization", "Bearer "+token)
                .asString();
        System.out.println("Testing bad GET : "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(404, response.getStatus());

        response = Unirest.post("http://localhost:1310/api/good/1")
                .header("Content-Type", "text/plain")
                .header("Authorization", "Bearer "+token)
                .body("{\"id\":1,\"name\":\"22\",\"price\": 3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good POST : "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(204, response.getStatus());

        response = Unirest.post("http://localhost:1310/api/good/1")
                .header("Content-Type", "text/plain")
                .header("Authorization", "Bearer f"+token.substring(1))
                .body("{\"id\":1,\"name\":\"22\",\"price\": 3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good POST with bad token: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(401, response.getStatus());

        response = Unirest.post("http://localhost:1310/api/good/1000000")
                .header("Content-Type", "text/plain")
                .header("Authorization", "Bearer "+token)
                .body("{\"id\":1,\"name\":\"22\",\"price\": 3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing good POST with bad id: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(404, response.getStatus());

        response = Unirest.post("http://localhost:1310/api/good/1")
                .header("Content-Type", "text/plain")
                .header("Authorization", "Bearer "+token)
                .body("{\"id\":1,\"name\":\"22\",\"price\": -3.0,\"quantity\":4,\"category\":\"5\"}")
                .asString();
        System.out.println("Testing bad POST: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(409, response.getStatus());

        response = Unirest.delete("http://localhost:1310/api/good/1")
                .header("Authorization", "Bearer f"+token.substring(1))
                .asString();
        System.out.println("Testing good DELETE bad token: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(401, response.getStatus());

        response = Unirest.delete("http://localhost:1310/api/good/11111111")
                .header("Authorization", "Bearer "+token)
                .asString();
        System.out.println("Testing bad DELETE: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(404, response.getStatus());

        response = Unirest.delete("http://localhost:1310/api/good/1")
                .header("Authorization", "Bearer "+token)
                .asString();
        System.out.println("Testing good DELETE: "+response.getStatus()+" "+response.getBody());
        Assert.assertEquals(204, response.getStatus());

        Server.kill();
    }

}
