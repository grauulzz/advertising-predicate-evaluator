import com.amazon.ata.customerservice.State;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.*;
import com.github.stefanbirkner.systemlambda.Statement;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class LocalDbTest {
    // idea, write a createtable from json converter
    private final AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().withEndpointConfiguration(
                    new AwsClientBuilder.EndpointConfiguration("http://localhost:8000",
                            "us-east-1")).build();
    DynamoDB dynamoDB = new DynamoDB(client);
    private final GsonBuilder builder = new GsonBuilder();
    private final Gson gson = builder.create();
    private final Gson gsonPty = builder.setPrettyPrinting().create();


    Consumer<String> systemLambda = exe -> {
        Statement statement = () -> System.out.println(exe);
        try {
            statement.execute();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    };

    private Consumer<Object> printJson = o -> System.out.println(gson.toJson(o));
    private Consumer<Object> printJsonPty = o -> {
        String teal = "\u001B[36m"; String json = gsonPty.toJson(o); String[] lines = json.split("\n");
        StringBuilder sb = new StringBuilder();
        sb.append(teal);
        for (String line : lines) {
            sb.append(line).append("\n").append(teal);
        }
        System.out.println(sb + "\u001B[0m");
    };


    void createTable() {
        ArrayList<AttributeDefinition> attributeDefinitions = new ArrayList<AttributeDefinition>();
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("ContentId").withAttributeType("S"));
        attributeDefinitions.add(new AttributeDefinition().withAttributeName("MarketplaceId").withAttributeType("S"));

        ArrayList<KeySchemaElement> tableKeySchema = new ArrayList<KeySchemaElement>();
        tableKeySchema.add(new KeySchemaElement().withAttributeName("ContentId").withKeyType(KeyType.HASH));

        GlobalSecondaryIndex precipIndex = new GlobalSecondaryIndex();
        precipIndex.withIndexName("MarketplaceIdIndex")
                .withProvisionedThroughput(new ProvisionedThroughput()
                                                   .withReadCapacityUnits((long) 10)
                                                   .withWriteCapacityUnits((long) 1))
                .withProjection(new Projection().withProjectionType(ProjectionType.ALL));

        CreateTableRequest createTableRequest = new CreateTableRequest()
                .withTableName("Content")
                        .withProvisionedThroughput(new ProvisionedThroughput()
                                                           .withReadCapacityUnits((long) 5)
                                                           .withWriteCapacityUnits((long) 1))
                .withAttributeDefinitions(attributeDefinitions)
                .withKeySchema(tableKeySchema)
                .withGlobalSecondaryIndexes(precipIndex);

        Table table = dynamoDB.createTable(createTableRequest);
        System.out.println(table);
    }

    void batchWriteFromJson() {
        File file = new File("configurations/cloudFormation/content_table.json");
    }

    void deleteTable() {
        DeleteTableRequest request = new DeleteTableRequest()
                .withTableName("Content");

        client.deleteTable(request);
    }

    @Test
    void system_lambda() throws Exception {
        String text = tapSystemOut(() -> {
            System.out.println(".");
        });
        Assert.assertEquals( text.trim(), ".");
    }

    @Test
    void getFile() throws FileNotFoundException {
        File file = new File("configurations/cloudFormation/content_table.json");
        Reader reader = new FileReader(file);
        JsonReader jsonReader = new JsonReader(reader);
        JsonParser parser = new JsonParser();
        JsonElement jsonElement = parser.parse(jsonReader);
        printJsonPty.accept(jsonElement);
        assertNotNull(jsonElement);
    }

    @Test
    void testCreateTable() {
        createTable();
    }

    @Test
    void testDeleteTable() {
        deleteTable();
    }


    @Test
    void run() {
        System.out.println("Running test");

    }
}
