# BTVR

(Potentially a temporary name, unsure at the moment)

BTVR is a heavily work in progress VR mod for BTA

I'm not sure what else to put here, lol

## Prerequisites
- JDK for Java 17 ([Eclipse Temurin](https://adoptium.net/temurin/releases/) recommended)
- IntelliJ IDEA
- Minecraft Development plugin (Optional, but highly recommended)

## Using Dev Env
   
1. Create a new run configuration by going in `Run > Edit Configurations`.  
   Then click on the plus icon and select Gradle. In the `Tasks and Arguments` field enter `build`.  
   Running it will build your finished jar files and put them in `build/libs/`.

2. While in the same place, select the Client and Server run configurations and edit the VM options under the SDK selection.

   ![image](https://github.com/Turnip-Labs/bta-example-mod/assets/58854399/2d45551d-83e3-4a75-b0e6-acdbb95b8114)  

   Click the double arrow icon to expand the list, and append `-Dfabric.gameVersion=1.7.7.0` to the end.  

   ![image](https://github.com/Turnip-Labs/bta-example-mod/assets/58854399/e4eb8a22-d88a-41ef-8fb2-e37c66e18585)

3. Lastly, open `File` > `Settings` and head to `Build, Execution, Development` > `Build Tools` > `Gradle`.  
   Make sure `Build and run using` and `Run tests using` is set to `Gradle`.
