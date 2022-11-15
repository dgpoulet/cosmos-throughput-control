# Limit Cosmos DB Throughput in Java

This demonstrates the use of Throughput Control Groups in the Cosmos DB Java SDK to limit the amount of RUs consumed by processes. This is very useful in situations where you have an occasional activity like a data load that needs to run without impacting the throughput available for normal processing.
