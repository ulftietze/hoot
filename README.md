Sie haben in einem Verzeichnis
das kleine tomcat-Kit installiert.

Sie haben hier vier kleine Skripte:

- mkanddeploy.sh
  kopiert app/* nach build,
  kompiliert die java-Dateien in src
  in das Verzeichnis build/WEB-INF/classes
  hinein und deployt mit curl über die
  manager-App in tomcat mit den
  crendentials in .netrc

- clean.sh
  löscht das build-Verzeichnis 
  und die war-Datei

- undeploy.sh
  undeployt die App

- list.sh
  listet die aktiven WebApps auf

In src liegt das java-Paket hbv
mit Java-Beispieldateien.

In app befindet sich die 
Deployment-Struktur mit
html-Dateien
WEB-INF, web.xml
und den notwendigen Bibliotheken
für die Laufzeit.

In complibs befinden sich die jars,
die zum Compilieren notwendig sind:
servlets. 

