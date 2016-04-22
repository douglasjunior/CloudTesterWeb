# CloudTesterWeb

Implementação da biblioteca [CloudTester](https://github.com/douglasjunior/CloudTester) para comparação entre APIs de acesso a plataformas de nuvem.

## Funcionalidades previstas

![Funcionalidades](https://raw.githubusercontent.com/douglasjunior/CloudTesterWeb/master/ImgProcSystem.jpg)

## APIs disponíveis
- [jclouds](https://jclouds.apache.org/)
- [Azure Storage](https://azure.microsoft.com/pt-br/develop/java/)
- [Amazon AWS](https://aws.amazon.com/pt/sdk-for-java/)

## Plataformas suportadas
- [Microsoft Azure](https://azure.microsoft.com/pt-br/)
- [Amazon Web Service (AWS)](http://aws.amazon.com/)

## Funções implementadas
- Armazenamento de arquivos (Blob)
 - Download
 - Upload
 
## Instruções de uso
1. Crie um arquivo chamado `credentials.properties` no diretório `/src/main/resources` para armazenar os atributos de autenticação.

 Exemplo:
 
 ```properties
 ### AZURE
 IDENTITY_AZURE = cloudtester #[nome do Serviço Blob]
 CREDENTIAL_AZURE = T8pUMVXjE+QRC8c5tdwn/ffpsdsPOUlJBjLg98Jyl2qOxWmBxThewERoDENBLgYahjkX9fYlfzLywSO/aps== #[ID da Assinatura do Serviço Blob]
 CONTAINER_NAME_AZURE = cloudtester #[nome do container criado]
 ### AMAZON
 IDENTITY_AWS = ETOEJY4S8VB2AUG5L84B #[Access Key ID criada na seção de Security Credentials]
 CREDENTIAL_AWS = 8+NeuyABHDop7WuIjP1Xs6+7mRZOhauIJgom2vWz #[Secret Access Key criada na seção de Security Credentials]
 CONTAINER_NAME_AWS = cloudtester-utfpr #[nome do Bucket criado no S3]
 ```
 *Obs: Substitua os valores de exemplo pelos valores de suas credenciais. Não compartilhe suas credenciais com terceiros.*
 
2. Configure o JDBC e o Datasource no servidor `Wildfly 10`. Exemplo [aqui](https://github.com/douglasjunior/TestWildfly).

3. Implante a ferramenta em um servidor `Wildfly 10 Java EE 7 Full`

## Considerações
- Devido a uma incompatibilidade entre a `jclouds` e o `WELD`, a ferramenta não pode ser implantada no `Glassfish`, por isso a escolha do `Wildfly`.
- A implementação `JPA` disponível no `Wildfly` por padrão é o `Hibernate`, e na tentativa de configuração do `Eclipselink` vários problemas foram encontrados. Vide [guia de referência](https://docs.jboss.org/author/display/WFLY10/JPA+Reference+Guide#JPAReferenceGuide-UsingEclipseLink)

 
 
