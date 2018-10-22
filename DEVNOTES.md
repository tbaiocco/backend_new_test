I hope I can understand correctly what you've asked to be done... ;)
So, in case I had misunderstood or let doubts, you can ask me, ok? 

- In the early contact with the skeleton I had noticed that wasn't running, because a @Value in API.class and no application.properties in the project. To fix this I place a proper application.properties with the missing entry.
- After that I'd saw the tests wasn't running thru Maven. Well this was because the latest versions of surefire does not deal 'nicely' with maven to run JUnit5 tests. So, I correct that adding maven-surefire plugin in pom.xml with the best version with running capability in this case (2.19.1). 

I think these was in purpose, to catch some sleepy developers.

To check if the order is further than 5Km of courier I'm take courier position to pickup location. It seems the logical way, to me. I think than can be another checking to avoid bicycle courier to take orders with destination further than 5Km of pickup location as well, but I haven't saw that in WORDING.md.

I had made some choices in this development, and tried some different approaches. I left placed in github project a version with application.properties and other with application.yml.
I do prefer, particularly, yaml to be less verbose and more readable.
You will find other endpoints I made just for checking and having a better view of the data.

I'd configured Swagger UI on the project because is fast and easy, and I think it provides a nice and visual interface to test services inside the browser without needing other tools like curl.  

As I said before, I made some choices in this development. Most of them was taken bring more comfort to me, as techniques I was used before, as tools I know and so on.

I will hope the code I made fit your expectations and is accurate as you want to.

- You can execute `./run` and test thru [Swagger UI](http://localhost:8080/swagger-ui.html).
- For testing I place a `./test` file to make easier to run tests.

Thanks!
Téo
