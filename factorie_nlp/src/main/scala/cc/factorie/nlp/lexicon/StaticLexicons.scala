package cc.factorie.nlp.lexicon

import cc.factorie.nlp.lexicon.{iesl => Iesl, ssdi => Ssdi, uscensus => Uscensus, wikipedia => Wikipedia}

class StaticLexicons()(implicit lp:LexiconsProvider) {

  import lp._

  object iesl {

    object Continents extends Iesl.Continents()(lp.provide[Iesl.Continents])

    object Country extends Iesl.Country()(lp.provide[Iesl.Country])

    object City extends Iesl.City()(lp.provide[Iesl.City])

    object UsState extends Iesl.UsState()(lp.provide[Iesl.UsState])

    object PlaceSuffix extends Iesl.PlaceSuffix()(lp.provide[Iesl.PlaceSuffix])

    object JobTitle extends Iesl.JobTitle()(lp.provide[Iesl.JobTitle])

    object Money extends Iesl.Money()(lp.provide[Iesl.Money])

    object Company extends Iesl.Company()(lp.provide[Iesl.Company])

    object OrgSuffix extends Iesl.OrgSuffix()(lp.provide[Iesl.OrgSuffix])

    object Month extends Iesl.Month()(lp.provide[Iesl.Month])

    object Day extends Iesl.Day()(lp.provide[Iesl.Day])

    object PersonHonorific extends Iesl.PersonHonorific()(lp.provide[Iesl.PersonHonorific])

    object PersonFirstHighest extends Iesl.PersonFirstHighest()(lp.provide[Iesl.PersonFirstHighest])

    object PersonFirstHigh extends Iesl.PersonFirstHigh()(lp.provide[Iesl.PersonFirstHigh])

    object PersonFirstMedium extends Iesl.PersonFirstMedium()(lp.provide[Iesl.PersonFirstMedium])

    object PersonLastHighest extends Iesl.PersonLastHighest()(lp.provide[Iesl.PersonLastHighest])

    object PersonLastHigh extends Iesl.PersonLastHigh()(lp.provide[Iesl.PersonLastHigh])

    object PersonLastMedium extends Iesl.PersonLastMedium()(lp.provide[Iesl.PersonLastMedium])

    object Say extends Iesl.Say()(lp.provide[Iesl.Say])

    object Demonym extends Iesl.Demonym()(lp.provide[Iesl.Demonym])

    object DemonymMap extends Iesl.DemonymMap()(lp.provide[Iesl.Demonym])

    object AllPlaces extends TrieUnionLexicon("places", Continents, Country, City, UsState)

    object PersonFirst extends TrieUnionLexicon("person-first", PersonFirstHighest, PersonFirstHigh, PersonFirstMedium)

    object PersonLast extends TrieUnionLexicon("person-last", PersonLastHighest, PersonLastHigh, PersonLastMedium)

  }

  object uscensus {

    object PersonFirstFemale extends Uscensus.PersonFirstFemale()(lp.provide[Uscensus.PersonFirstFemale])
    object PersonFirstMale extends Uscensus.PersonFirstMale()(lp.provide[Uscensus.PersonFirstMale])
    object PersonLast extends Uscensus.PersonLast()(lp.provide[Uscensus.PersonLast])

  }

  object wikipedia {
    object Battle extends Wikipedia.Battle()(lp.provide[Wikipedia.Battle])
    object BattleRedirect extends Wikipedia.BattleRedirect()(lp.provide[Wikipedia.BattleRedirect])
    object BattleAndRedirect extends TrieUnionLexicon("battle-and-redirect", Battle, BattleRedirect)
    object BattleDisambiguation extends Wikipedia.BattleDisambiguation()(lp.provide[Wikipedia.BattleDisambiguation])
    object Book extends Wikipedia.Book()(lp.provide[Wikipedia.Book])
    object BookRedirect extends Wikipedia.BookRedirect()(lp.provide[Wikipedia.BookRedirect])
    object BookAndRedirect extends TrieUnionLexicon("book-and-redirect", Book, BookRedirect)
    object BookDisambiguation extends Wikipedia.BookDisambiguation()(lp.provide[Wikipedia.BookDisambiguation])
    object Business extends Wikipedia.Business()(lp.provide[Wikipedia.Business])
    object BusinessRedirect extends Wikipedia.BusinessRedirect()(lp.provide[Wikipedia.BusinessRedirect])
    object BusinessAndRedirect extends TrieUnionLexicon("business-and-redirect", Business, BusinessRedirect)
    object BusinessDisambiguation extends Wikipedia.BusinessDisambiguation()(lp.provide[Wikipedia.BusinessDisambiguation])
    object Competition extends Wikipedia.Competition()(lp.provide[Wikipedia.Competition])
    object CompetitionRedirect extends Wikipedia.CompetitionRedirect()(lp.provide[Wikipedia.CompetitionRedirect])
    object CompetitionAndRedirect extends TrieUnionLexicon("competition-and-redirect", Competition, CompetitionRedirect)
    object CompetitionDisambiguation extends Wikipedia.CompetitionDisambiguation()(lp.provide[Wikipedia.CompetitionDisambiguation])
    object Event extends Wikipedia.Event()(lp.provide[Wikipedia.Event])
    object EventRedirect extends Wikipedia.EventRedirect()(lp.provide[Wikipedia.EventRedirect])
    object EventAndRedirect extends TrieUnionLexicon("event-and-redirect", Event, EventRedirect)
    object EventDisambiguation extends Wikipedia.EventDisambiguation()(lp.provide[Wikipedia.EventDisambiguation])
    object Film extends Wikipedia.Film()(lp.provide[Wikipedia.Film])
    object FilmRedirect extends Wikipedia.FilmRedirect()(lp.provide[Wikipedia.FilmRedirect])
    object FilmAndRedirect extends TrieUnionLexicon("film-and-redirect", Film, FilmRedirect)
    object FilmDisambiguation extends Wikipedia.FilmDisambiguation()(lp.provide[Wikipedia.FilmDisambiguation])
    object Location extends Wikipedia.Location()(lp.provide[Wikipedia.Location])
    object LocationRedirect extends Wikipedia.LocationRedirect()(lp.provide[Wikipedia.LocationRedirect])
    object LocationAndRedirect extends TrieUnionLexicon("location-and-redirect", Location, LocationRedirect)
    object LocationDisambiguation extends Wikipedia.LocationDisambiguation()(lp.provide[Wikipedia.LocationDisambiguation])
    object ManMadeThing extends Wikipedia.ManMadeThing()(lp.provide[Wikipedia.ManMadeThing])
    object ManMadeThingRedirect extends Wikipedia.ManMadeThingRedirect()(lp.provide[Wikipedia.ManMadeThingRedirect])
    object ManMadeThingAndRedirect extends TrieUnionLexicon("man-made-thing-and-redirect", ManMadeThing, ManMadeThingRedirect)
    object ManMadeThingDisambiguation extends Wikipedia.ManMadeThingDisambiguation()(lp.provide[Wikipedia.ManMadeThingDisambiguation])
    object Organization extends Wikipedia.Organization()(lp.provide[Wikipedia.Organization])
    object OrganizationRedirect extends Wikipedia.OrganizationRedirect()(lp.provide[Wikipedia.OrganizationRedirect])
    object OrganizationAndRedirect extends TrieUnionLexicon("organization-and-redirect", Organization, OrganizationRedirect)
    object OrganizationDisambiguation extends Wikipedia.OrganizationDisambiguation()(lp.provide[Wikipedia.OrganizationDisambiguation])
    object Person extends Wikipedia.Person()(lp.provide[Wikipedia.Person])
    object PersonRedirect extends Wikipedia.PersonRedirect()(lp.provide[Wikipedia.PersonRedirect])
    object PersonAndRedirect extends TrieUnionLexicon("person-and-redirect", Person, PersonRedirect)
    object PersonDisambiguation extends Wikipedia.PersonDisambiguation()(lp.provide[Wikipedia.PersonDisambiguation])
    object Song extends Wikipedia.Song()(lp.provide[Wikipedia.Song])
    object SongRedirect extends Wikipedia.SongRedirect()(lp.provide[Wikipedia.SongRedirect])
    object SongAndRedirect extends TrieUnionLexicon("song-and-redirect", Song, SongRedirect)
    object SongDisambiguation extends Wikipedia.SongDisambiguation()(lp.provide[Wikipedia.SongDisambiguation])

  }

  object ssdi {
    object PersonFirstHighest extends Ssdi.PersonFirstHighest()(lp.provide[Ssdi.PersonFirstHighest])
    object PersonFirstHigh extends Ssdi.PersonFirstHigh()(lp.provide[Ssdi.PersonFirstHigh])
    object PersonFirstMedium extends Ssdi.PersonFirstMedium()(lp.provide[Ssdi.PersonFirstMedium])
    object PersonLastHighest extends Ssdi.PersonLastHighest()(lp.provide[Ssdi.PersonLastHighest])
    object PersonLastHigh extends Ssdi.PersonLastHigh()(lp.provide[Ssdi.PersonLastHigh])
    object PersonLastMedium extends Ssdi.PersonLastMedium()(lp.provide[Ssdi.PersonLastMedium])

    object PersonFirst extends TrieUnionLexicon("person-first", PersonFirstHighest, PersonFirstHigh, PersonFirstMedium)

    object PersonLast extends TrieUnionLexicon("person-last", PersonLastHighest, PersonLastHigh, PersonLastMedium)

  }

}