package org.example;

import com.azure.cosmos.*;
import com.azure.cosmos.models.*;
import org.apache.commons.lang3.RandomStringUtils;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.UUID;


/**
 * Throughput Control Example
 *
 */
public class App 
{
    private CosmosAsyncClient clientasync;
    private CosmosAsyncDatabase database;
    private CosmosAsyncContainer container;

    private static final String ENDPOINT="<COSMOS_DB_ENDPOINT_URL>";
    private static final String KEY="<COSMOS_DB_ACCESS_KEY>";
    private static final String DATABASE_NAME="<DATABASE_NAME>";
    private static final String CONTAINER_NAME="<CONTAINER_NAME>";


    public static void main( String[] args )
    {

        Integer BATCHSIZE=100000; // Size of bulkload batch
        Integer THROUGHPUT_LIMIT=2000; // RU limit to be applied

        App myapp = new App();
        myapp.loadData(BATCHSIZE, THROUGHPUT_LIMIT);
    }


    /*
     * Do a bulk load of data, controlled using ThroughputControlGroup
     */
    public void loadData(Integer batchsize, Integer throughputLimit)
    {

        this.clientasync = new CosmosClientBuilder()
                .endpoint(ENDPOINT)
                .key(KEY)
                .consistencyLevel(ConsistencyLevel.EVENTUAL)
                .buildAsyncClient();

        this.database = this.clientasync.getDatabase(DATABASE_NAME);
        this.container = this.database.getContainer(CONTAINER_NAME);

        /*
         * Set up the Throughput Control Group definition
         */
        ThroughputControlGroupConfig groupConfig =
                new ThroughputControlGroupConfigBuilder()
                        .groupName("localControlGroup")
                        .targetThroughput(throughputLimit)
                        .defaultControlGroup(false)
                        .build();

             
        /*
         * Enable throughput control 
         */
        container.enableLocalThroughputControlGroup(groupConfig);

        /*
         * Generate a big batch of Customer data to load
         */
        ArrayList<Customer> doclist = new ArrayList<Customer>();

        for(int x=0; x<batchsize; x++)
        {
            Customer cx = new Customer();
            UUID uid = UUID.randomUUID();

            cx.setId(uid.toString());
            cx.setFirstName("Testy");
            cx.setLastName("McTestface");
            cx.setLocation("Testopolis");
            cx.setCustomerID(12345);
            cx.setRandomNotes(RandomStringUtils.randomAlphabetic(512));
            doclist.add(cx);
        }

        /*
         * Create a CosmosBulkExecutionOptions object defining the Throughput Control Group that will apply
         */
        CosmosBulkExecutionOptions bulkoptions = new CosmosBulkExecutionOptions().setThroughputControlGroupName("localControlGroup");


        /*
         * Kick off the bulk load operation on the batch of Customer data created above
         * passing the bulk options object above into the operation invocation
         */
        Flux<Customer> customers = Flux.fromIterable(doclist);

        Flux<CosmosItemOperation> cosmosItemOperations = customers.map(
                customer -> CosmosBulkOperations.getCreateItemOperation(customer, new PartitionKey(customer.getId())));
        container.executeBulkOperations(cosmosItemOperations, bulkoptions).blockLast();


        /*
        * Close the connection
        */
        clientasync.close();
    }
}
