# Limit Cosmos DB Throughput in Java

This demonstrates the use of Local Throughput Control Groups in the Cosmos DB Java SDK to limit the amount of RUs consumed by processes. This is very useful in situations where you have an occasional activity like a data load that needs to run without impacting the throughput available for normal processing.

This example shows a Bulk Write process which is configured to only use THROUGHPUT_LIMIT RUs while it is loading data, preventing the process from potentially consuming all the RUs provisioned and impacting other services using the container at the same time.

The key pieces of code are:

```java
        ThroughputControlGroupConfig groupConfig =
                new ThroughputControlGroupConfigBuilder()
                        .groupName("localControlGroup")
                        .targetThroughput(throughputLimit)
                        .defaultControlGroup(false)
                        .build();
         
        container.enableLocalThroughputControlGroup(groupConfig);
        
```
This sets up a control group, where the throughput control is configured. 

 **groupName(**_Group Name_**)** can be anything, it's used to refer to the group later on. This enables the possiblity to have multiple groups with different conifigurations active at once. 

 **targetThroughput(**_Number of RUs_**)**  defines how many RUs this control group will be limited to. It's also possible to use **targetThroughputThreshold(**_Proportion of Max RUs_**)** to define the limit as a fraction of the maximum RUs allocated to the container (a number between 0.0-1.0).
 
 **defaultControlGroup(**_True/False_**)** defines if this control group will be the default i.e. ANY the restrictions defined here will be applied to any operation against the container which DOESN'T have a specific control group nominated. 
 
 **container.enableLocalThroughoutControlGroup(**_Group Name_**)**  once the control group is defined, it must be enabled against the container instance we are using to run our operations.
 
 Once this is done, we can define any operations as being controlled by this Throughput Control Group, and the SDK will automatically apply client-side limiting of those operations to meet the RU restriction defined:
 
  
 ```java
      CosmosBulkExecutionOptions bulkoptions = new CosmosBulkExecutionOptions().setThroughputControlGroupName("localControlGroup");
      
      [ ... ]
      
      Flux<CosmosItemOperation> cosmosItemOperations = customers.map(
                customer -> CosmosBulkOperations.getCreateItemOperation(customer, new PartitionKey(customer.getId())));
                
      container.executeBulkOperations(cosmosItemOperations, bulkoptions).blockLast();
```
 For the bulk exector, we use **CosmosBulkExecutionOptions** to define the options we want to pass to the bulk executor, in this case passing the name of the throughput control group we want to apply.
 
 Then when we run the **executeBulkOperations(...)** method, we pass the options object in to enforce the use of throughput control.
 
 See the [SDK documentation](https://azuresdkdocs.blob.core.windows.net/$web/java/azure-cosmos/latest/com/azure/cosmos/ThroughputControlGroupConfigBuilder.html) for more information. 

## Note
There is also the concept of Global Throughput Control Groups that can be shared across many client instances using shared state in a Cosmos DB container. See [the SDK](https://azuresdkdocs.blob.core.windows.net/$web/java/azure-cosmos/latest/com/azure/cosmos/GlobalThroughputControlConfigBuilder.html) for details.
