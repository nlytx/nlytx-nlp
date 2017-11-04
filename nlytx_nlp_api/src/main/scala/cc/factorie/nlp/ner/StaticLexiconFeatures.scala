/* Copyright (C) 2008-2016 University of Massachusetts Amherst.
   This file is part of "FACTORIE" (Factor graphs, Imperative, Extensible)
   http://factorie.cs.umass.edu, http://github.com/factorie
   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0
   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License. */
package cc.factorie.nlp.ner

import cc.factorie.nlp.Token
import cc.factorie.nlp.lexicon.{LexiconsProvider, StaticLexicons}
import cc.factorie.variable.CategoricalVectorVar

class StaticLexiconFeatures(lexicon:StaticLexicons, lang: String) extends NerLexiconFeatures {
    //this block serves to initialize all of the lexicon used by the model before processing
  lexicon.synchronized {
    
    if (lang.equals("en") || lang.equals("all")){
      
    	lexicon.iesl.Month.toString()
    	lexicon.iesl.Day.toString()
    	
    	lexicon.iesl.PersonFirst.toString()
    	lexicon.iesl.PersonFirstHigh.toString()
    	lexicon.iesl.PersonFirstHighest.toString()
    	lexicon.iesl.PersonFirstMedium.toString()
    	
    	lexicon.iesl.PersonLast.toString()
    	lexicon.iesl.PersonLastHigh.toString()
    	lexicon.iesl.PersonLastHighest.toString()
    	lexicon.iesl.PersonLastMedium.toString()
    	
    	lexicon.iesl.PersonHonorific.toString()
    	
    	lexicon.iesl.Company.toString()
    	lexicon.iesl.JobTitle.toString()
    	lexicon.iesl.OrgSuffix.toString()
    	
    	lexicon.iesl.Country.toString()
    	lexicon.iesl.City.toString()
    	lexicon.iesl.PlaceSuffix.toString()
    	lexicon.iesl.UsState.toString()
    	lexicon.iesl.Continents.toString()
    	
    	lexicon.wikipedia.Person.toString()
    	lexicon.wikipedia.Event.toString()
    	lexicon.wikipedia.Location.toString()
    	lexicon.wikipedia.Organization.toString()
    	lexicon.wikipedia.ManMadeThing.toString()
    	lexicon.iesl.Demonym.toString()
    	
    	lexicon.wikipedia.Book.toString()
    	lexicon.wikipedia.Business.toString()
    	lexicon.wikipedia.Film.toString()
    	
    	lexicon.wikipedia.LocationAndRedirect.toString()
    	lexicon.wikipedia.PersonAndRedirect.toString()
    	lexicon.wikipedia.OrganizationAndRedirect.toString()
    }

		/* Removed Spanish language for this api
//    if (lang.equals("es") || lang.equals("all")){
//
//      lexicon.spanish.Continents.toString()
//      lexicon.spanish.Month.toString()
//      lexicon.spanish.Day.toString()
//      lexicon.spanish.PersonFirst.toString()
//      lexicon.spanish.PersonLast.toString()
//      lexicon.spanish.Person.toString()
//      lexicon.spanish.PersonHonorific.toString()
//      lexicon.spanish.Location.toString()
//      lexicon.spanish.Organization.toString()
//      lexicon.spanish.Miscellaneous.toString()
//      lexicon.spanish.OrgSuffix.toString()
//      lexicon.spanish.Demonym.toString()
//
//
//      lexicon.spanish.WikiBook.toString()
//      lexicon.spanish.WikiEvent.toString()
//      lexicon.spanish.WikiBusiness.toString()
//      lexicon.spanish.WikiFilm.toString()
//
//      lexicon.spanish.WikiPerson.toString()
//      lexicon.spanish.WikiLocation.toString()
//      lexicon.spanish.WikiOrganization.toString()
//      lexicon.spanish.WikiLocationAndRedirect.toString()
//      lexicon.spanish.WikiPersonAndRedirect.toString()
//      lexicon.spanish.WikiOrganizationAndRedirect.toString()

//    }
     */
  }
  
  
  
  def addLexiconFeatures(tokenSequence: IndexedSeq[Token], vf: (Token => CategoricalVectorVar[String])) {
    
    if (lang.equals("en") || lang.equals("all")){
         
    	lexicon.iesl.Month.tagText(tokenSequence,vf,"MONTH")
    	lexicon.iesl.Day.tagText(tokenSequence,vf,"DAY")
    	
    	lexicon.iesl.PersonFirst.tagText(tokenSequence,vf,"PERSON-FIRST")
    	lexicon.iesl.PersonFirstHigh.tagText(tokenSequence,vf,"PERSON-FIRST-HIGH")
    	lexicon.iesl.PersonFirstHighest.tagText(tokenSequence,vf,"PERSON-FIRST-HIGHEST")
    	lexicon.iesl.PersonFirstMedium.tagText(tokenSequence,vf,"PERSON-FIRST-MEDIUM")
    	
    	lexicon.iesl.PersonLast.tagText(tokenSequence,vf,"PERSON-LAST")
    	lexicon.iesl.PersonLastHigh.tagText(tokenSequence,vf,"PERSON-LAST-HIGH")
    	lexicon.iesl.PersonLastHighest.tagText(tokenSequence,vf,"PERSON-LAST-HIGHEST")
    	lexicon.iesl.PersonLastMedium.tagText(tokenSequence,vf,"PERSON-LAST-MEDIUM")
    	
    	lexicon.iesl.PersonHonorific.tagText(tokenSequence,vf,"PERSON-HONORIFIC")
    	
    	lexicon.iesl.Company.tagText(tokenSequence,vf,"COMPANY")
    	lexicon.iesl.JobTitle.tagText(tokenSequence,vf,"JOB-TITLE")
    	lexicon.iesl.OrgSuffix.tagText(tokenSequence,vf,"ORG-SUFFIX")
    	
    	lexicon.iesl.Country.tagText(tokenSequence,vf,"COUNTRY")
    	lexicon.iesl.City.tagText(tokenSequence,vf,"CITY")
    	lexicon.iesl.PlaceSuffix.tagText(tokenSequence,vf,"PLACE-SUFFIX")
    	lexicon.iesl.UsState.tagText(tokenSequence,vf,"USSTATE")
    	lexicon.iesl.Continents.tagText(tokenSequence,vf,"CONTINENT")
    	
    	lexicon.wikipedia.Person.tagText(tokenSequence,vf,"WIKI-PERSON")
    	lexicon.wikipedia.Event.tagText(tokenSequence,vf,"WIKI-EVENT")
    	lexicon.wikipedia.Location.tagText(tokenSequence,vf,"WIKI-LOCATION")
    	lexicon.wikipedia.Organization.tagText(tokenSequence,vf,"WIKI-ORG")
    	lexicon.wikipedia.ManMadeThing.tagText(tokenSequence,vf,"MANMADE")
    	lexicon.iesl.Demonym.tagText(tokenSequence,vf,"DEMONYM")
    	
    	lexicon.wikipedia.Book.tagText(tokenSequence,vf,"WIKI-BOOK")
    	lexicon.wikipedia.Business.tagText(tokenSequence,vf,"WIKI-BUSINESS")
    	lexicon.wikipedia.Film.tagText(tokenSequence,vf,"WIKI-FILM")
    	
    	lexicon.wikipedia.LocationAndRedirect.tagText(tokenSequence,vf,"WIKI-LOCATION-REDIRECT")
    	lexicon.wikipedia.PersonAndRedirect.tagText(tokenSequence,vf,"WIKI-PERSON-REDIRECT")
    	lexicon.wikipedia.OrganizationAndRedirect.tagText(tokenSequence,vf,"WIKI-ORG-REDIRECT")
    }
/*
    if (lang.equals("es") || lang.equals("all")){
      
      lexicon.spanish.Continents.tagText(tokenSequence,vf,"CONTINENT")
      lexicon.spanish.Month.tagText(tokenSequence,vf,"MONTH")
      lexicon.spanish.Day.tagText(tokenSequence,vf,"DAY")
      lexicon.spanish.PersonFirst.tagText(tokenSequence,vf,"PERSON-FIRST")
      lexicon.spanish.PersonLast.tagText(tokenSequence,vf,"PERSON-LAST")
      lexicon.spanish.Person.tagText(tokenSequence,vf,"PERSON")
      lexicon.spanish.Location.tagText(tokenSequence,vf,"LOCATION")
      lexicon.spanish.Organization.tagText(tokenSequence,vf,"ORGANIZATION")
      lexicon.spanish.Miscellaneous.tagText(tokenSequence,vf,"MISCELLANEOUS")
      lexicon.spanish.PersonHonorific.tagText(tokenSequence,vf,"PERSON-HONORIFIC")
      lexicon.spanish.OrgSuffix.tagText(tokenSequence,vf,"ORG-SUFFIX")
      lexicon.spanish.Demonym.tagText(tokenSequence,vf,"DEMONYM")
  
      
      lexicon.spanish.WikiBook.tagText(tokenSequence,vf,"WIKI-BOOK")
      lexicon.spanish.WikiEvent.tagText(tokenSequence,vf,"WIKI-EVENT")
      lexicon.spanish.WikiFilm.tagText(tokenSequence,vf,"WIKI-FILM")
      lexicon.spanish.WikiBusiness.tagText(tokenSequence,vf,"WIKI-BUSINESS")
      
      lexicon.spanish.WikiPerson.tagText(tokenSequence,vf,"WIKI-PERSON")
      lexicon.spanish.WikiLocation.tagText(tokenSequence,vf,"WIKI-LOCATION")
      lexicon.spanish.WikiOrganization.tagText(tokenSequence,vf,"WIKI-ORG")
      lexicon.spanish.WikiLocationAndRedirect.tagText(tokenSequence,vf,"WIKI-LOCATION-REDIRECT")
      lexicon.spanish.WikiPersonAndRedirect.tagText(tokenSequence,vf,"WIKI-PERSON-REDIRECT")
      lexicon.spanish.WikiOrganizationAndRedirect.tagText(tokenSequence,vf,"WIKI-ORG-REDIRECT")
    }
*/
  }

}

object StaticLexiconFeatures{
  def apply(lang: String = "en"): StaticLexiconFeatures = {
    new StaticLexiconFeatures(new StaticLexicons()(LexiconsProvider.classpath()), lang)
  }
}