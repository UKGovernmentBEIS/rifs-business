# Mechanical generation if Slick table classes from case classes

This package contains tools that will let you generate all of the slick table code directly from a case class, saving hours of boring and error prone coding of boilerplate based on information you've already provided in the names and types of the fields of the case classes.

This is very much a work-in-progress and there are many improvements we can make.

There are certain conventions that are assumed:

* `camelCase` field names are converted to `snake_case` column names
* The primary key is named `id`
* Any field that has a type ending in `Id` is assumed to be a foreign key reference to another table with the same name with `Id` stripped off and `Table` stuck on the end. So a field of type `ApplicationId` is a reference to `applicationTable` 
* Foreign key references and indexes will be generated for these fields
* The id types are assumed to wrap `Long` and slick type mappers will be generated for them.

## Modules

One or more tables can be grouped into a `ModuleSpec` along with a name for the generated module class and a list of other modules it depends on.

## Module generation is done by hand

This tool is still a somewhat rough. In order to generate the code for a `ModuleSpec` you can run it's main class (inherited from `ModuleDefinition`) and the trait that implements the slick code will be written to `stdout`. You'll then need to copy this into an appropriate source file and provide the necessary imports. 

A generate module trait extends the `DBBinding` trait in order to provide the necessary declarations for the `JdbcProfile` and the `Database`.

To use a module trait you'll need to mix it into a class or object that provides the concrete implementation of a `DatabaseConfig` and also mixes in the traits for the dependent modules.

For example:

```
class ApplicationTables @Inject()(dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ApplicationModule with CompetitionModule with UserModule with DBBinding with ApplicationOps {
  override val dbConfig: DatabaseConfig[JdbcProfile] = dbConfigProvider.get[JdbcProfile]

  import driver.api._

  def statusById(applicationStatusId: ApplicationStatusId):Future[Option[ApplicationStatusRow]] = db.run {
    applicationStatusTable.filter(_.id === applicationStatusId).result.headOption
  }

  override def byId(id: ApplicationId): Future[Option[ApplicationRow]] = db.run{
    applicationTable.filter(_.id === id).result.headOption
  }
}
```

