Pizzacase Project
Het Pizzacase project is een geavanceerd systeem voor het bestellen van pizza's, ontworpen om een veilige en efficiënte ervaring voor gebruikers te bieden. Dit document beschrijft de kernontwerppatronen die in dit project worden gebruikt, waaronder Singleton, Composite, Visitor, SSL-encryptie en Message Authentication Code (MAC) mechanismen.

Ontwerppatronen
Singleton
Toepassing in het project:
De Singleton is geïmplementeerd in de Server klasse, die een centrale rol speelt in onze applicatie door de databaseverbinding en -operaties te beheren. Het patroon zorgt ervoor dat er slechts één instantie van de Server klasse kan bestaan, waardoor een uniform toegangspunt tot de database wordt gegarandeerd en onnodige instantiaties worden voorkomen.

Implementatie:
Privé Constructor: De constructor van de Server klasse is privé gemaakt om directe instantiatie van buitenaf te voorkomen.
Privé Statische Instantie: Een privé statische instantie van de Server klasse wordt intern bijgehouden.
Publieke Statische Methode: De getInstance() methode biedt een publiek toegangspunt tot de enkele instantie. Deze methode controleert of de interne instantie null is; zo ja, dan wordt een nieuwe instantie aangemaakt. Vervolgens retourneert het deze instantie.
