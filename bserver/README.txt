The API is very strict in what it will accept. Per the requirements, it will only accept:

-Level (1 – 85)
-Race (Orc, Tauren, Blood Elf, Human, Gnome, Worgen)
-Class (Warrior, Druid, Death Knight, Mage)
-Faction (Horde, Alliance)

and a created character must have all four parameters + a name

A valid command will return 200 and any valid data in XML format.

If an invalid command is entered (e.g. attempting to create a Human charater on the Horde faction), a
return status of 403 will be returned with no xml data.

Deleting an account will also delete characters associated with that account. Unlike deleting
a character, deleting an account will permanantly remove both the database and the characters.

Because an external premade server is being used, I can't install pthreads 
(https://github.com/krakjoe/pthreads) and as a result, I can't multithread in this application
. I do have some experience with multithreading though 
(https://github.com/DanielDowns, MalwareReverseEngineering/CSONserver and MiscProjects/NaturalSelectionSim)