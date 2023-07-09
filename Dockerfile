FROM adoptopenjdk:11-jdk-hotspot

# Instala o Git
RUN apt-get update && apt-get install -y git

# Diretório de trabalho
WORKDIR /trabalho-so

# Clona o repositório
RUN git clone https://github.com/Kev1nLopes/trabalho-so.git .

# Executa durante o processo de build
RUN javac src/main/java/Main/Main.java
RUN java -version
# Comando que será executado quando o container terminar de subir
ENTRYPOINT ["java", "-cp", "src/main/java", "Main.Main"]

EXPOSE 3000