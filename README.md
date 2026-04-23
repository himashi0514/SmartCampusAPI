# "Smart Campus" Sensor & Room Management API

## Project Overview
The Smart Campus API is a robust, highly available RESTful web service developed using Java and the JAX-RS (Jakarta RESTful Web Services) framework via Jersey. It is designed to manage university campus infrastructure, specifically tracking Rooms and the diverse array of hardware Sensors (e.g. Temperature, CO2) deployed within them. 

The architecture features:
* **Resource-Based Interactions:** Clean separation of `Rooms` and `Sensors` entities.
* **Sub-Resource Locators:** Deep nesting capabilities to handle historical `SensorReadings` tied to specific hardware.
* **Resilient Error Handling:** Custom Exception Mappers to prevent internal stack trace leaks and return semantically accurate HTTP status codes (e.g. 409 Conflict, 422 Unprocessable Entity, 403 Forbidden).


## Build and Launch Instructions
This project is managed via Maven and is configured to run on an Apache Tomcat servlet container.

**Prerequisites:**
* Java Development Kit (JDK) 11 or higher
* Apache Maven
* Apache Tomcat (or execution via NetBeans IDE)

**Step-by-Step Setup:**
1. Clone this repository to your local machine.
2. Open the project in Apache NetBeans IDE (or your preferred Java EE IDE).
3. Ensure your project's Run Configuration is set to deploy to Apache Tomcat.
4. Set the **Context Path** to `/SmartCampusAPI` (In NetBeans: Right-click Project -> Properties -> Run -> Context Path).
5. Right-click the project and select **Clean and Build** to allow Maven to download dependencies (JAX-RS API, Jersey Container).
6. Right-click the project and select **Run** to start the Tomcat server and deploy the `.war` artifact.

The API will be available at: `http://localhost:8080/SmartCampusAPI/api/v1/`


## Sample Interactions (cURL Commands)
Below are 5 sample `curl` commands demonstrating successful interactions with the core endpoints of the API.

**1. API Discovery (GET)**
Retrieve versioning and hypermedia links for primary resource collections.
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/

**2. Register a New Room (POST)**
Create a new room in the campus system.
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
-H "Content-Type: application/json" \
-d "{\"id\": \"LIB-301\", \"name\": \"Library Quiet Study\", \"capacity\": 50}"

**3. Retrieve All Rooms (GET)**
Fetch a list of all currently registered rooms.
curl -X GET http://localhost:8080/SmartCampusAPI/api/v1/rooms

**4. Register a New Sensor (POST)**
Deploy a new sensor and link it to an existing room via foreign key constraint.
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
-H "Content-Type: application/json" \
-d "{\"id\": \"TEMP-001\", \"type\": \"Temperature\", \"status\": \"ACTIVE\", \"roomId\": \"LIB-301\"}"

**5. Add a Sensor Reading (POST)**
Use the sub-resource locator to log a new historical data reading for a specific sensor.
curl -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/TEMP-001/readings \
-H "Content-Type: application/json" \
-d "{\"value\": 22.5}"


## Answers to the Conceptual questions (report answers)
Part 1: Service Architecture & Setup 
Part 1.1 - Project & Application Configuration: 
By default, JAX-RS resource classes follow a per-request lifecycle, meaning a new instance is created for each incoming HTTP request, unless explicitly configured as a singleton using @Singleton. As a result, instance variables are not shared across requests, which reduces the risk of thread interference at the object level. However, shared in-memory data structures (such as static maps or application-scoped storage) are still accessed concurrently by multiple threads and must therefore be properly synchronized. Using thread-safe collections like ConcurrentHashMap ensures atomic operations and maintains data integrity when multiple requests attempt to read or modify shared state simultaneously.

Part 1.2 - The ”Discovery” Endpoint: 
Hypermedia (HATEOAS) is considered a hallmark of advanced RESTful design because it makes the API self-documenting and discoverable. Instead of hardcoding URLs, client developers can rely on the dynamic links provided in the server's responses to navigate state transitions. This approach benefits client developers by decoupling the client from server-side routing changes, creating a strong and flexible architecture compared to relying solely on static documentation.


Part 2: Room Management
Part 2.1 – Room Resource Implementation: 
When returning a list of rooms, returning only IDs conserves network bandwidth and reduces server-side payload overhead. However, it shifts the processing burden to the client side, which must execute subsequent GET requests to retrieve necessary metadata. On the other hand, returning the full room objects eliminates those extra requests but increases the payload size.

Part 2.2 - RoomDeletion & Safety Logic: 
Yes, the DELETE operation is idempotent in this implementation. If a client mistakenly sends the exact same DELETE request for a room multiple times, the first request successfully removes the room (returning 204 No Content), and subsequent requests will find the room already deleted. Essentially, the server's state remains exactly the same after the second or third call as it was after the first call.


Part 3: Sensor Operations & Linking
Part 3.1 - Sensor Resource & Integrity: 
If a client attempts to send data in an unexpected format, such as text/plain or application/xml, JAX-RS automatically intercepts the mismatch before it reaches the resource method logic. It halts execution and returns an HTTP 415 Unsupported Media Type error to the client, strictly enforcing the API's technical data contract.

Part 3.2 - Filtered Retrieval & Search: 
Path parameters (e.g. /type/CO2) identify a specific, hierarchical resource location. The query parameter approach (?type=CO2) is generally considered superior for filtering and searching collections because it represents optional, non-hierarchical filters. This aligns perfectly with REST semantics, allowing clients to chain multiple filters simultaneously without breaking the core URI structure.


Part 4: Deep Nesting with Sub- Resources
Part 4.1 - The Sub-Resource Locator Pattern: 
The Sub-Resource Locator pattern in JAX-RS enables dynamic delegation based on URI hierarchy, where a parent resource returns a sub-resource to handle deeper paths. This supports lazy resolution, meaning the sub-resource is only created when that specific path is accessed.
It aligns with REST principles by reflecting real-world resource structure in the URI, while also enforcing separation of concerns. Compared to a single large controller, this approach keeps logic modular, making the API easier to maintain, extend, and test as it grows.


Part 5: Advanced Error Handling & Exception Mapping
Part 5.2 - Dependency Validation (422 Unprocessable Entity):
An HTTP 404 implies the requested endpoint URI itself does not exist. HTTP 422 (Unprocessable Entity) is considered semantically superior when the issue is a missing reference inside a valid JSON payload because it tells the client that the JSON is syntactically correct and readable, but logically flawed.

Part 5.4 - The Global Safety Net (500): 
Exposing internal Java stack traces to external API consumers presents severe cybersecurity risks, as an attacker could gather specific technical information such as exact library versions (allowing them to target known CVE vulnerabilities), uncover the underlying framework, and map out internal class structures and database logic flaws, vastly accelerating the reconnaissance phase of a cyberattack.
