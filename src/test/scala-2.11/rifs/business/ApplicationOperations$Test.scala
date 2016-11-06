package rifs.business

import org.scalatest._
import rifs.business.models._
import rifs.business.restmodels.OpportunityDuration
import rifs.business.tables.OpportunityExtractors

class ApplicationOperations$Test extends WordSpecLike with Matchers with OptionValues {

  //Save application section
  "Saving an Application Section" should {

    "Save a valid Appliation Section to the database and return a success" in {

    }

    //3 Don't save a blank Application section
    "Don't save Application Section with valid data and return success" in {

    }

    //Introduce back end validation rules ?

  }


  //Update Application section
  "Updating an Application Secton" should {
    //1 Basic successful update
    "Update an Application Section with valid data and return success" in {

    }

    //2 Save unchanged application section
    "Not udate an Application Section if there are no changes and return success" in {

    }

    //Introduce back end validation rules ?

  }


}


