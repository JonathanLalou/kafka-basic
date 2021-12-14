package com.github.jonathanlalou.kafkabasic.service;


import com.github.jonathanlalou.kafkabasic.domain.Els;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElsSequenceGeneratorTest {

    private String allLetters = StringUtils.remove("""
                    AndwhatdoyouthinkofthislatestcomedythecoronationatMilanaskedAnnaPavlovnaandofthecomedyofthepeopleofGenoaandLuccalayingtheirpetitionsbefore
                    MonsieurBuonaparteandMonsieurBuonapartesittingonathroneandgrantingthepetitionsofthenationsAdorableItisenoughtomakeonesheadwhirlItisasifth
                    ewholeworldhadgonecrazyPrinceAndrewlookedAnnaPavlovnastraightinthefacewithasarcasticsmileDieumeladonnegareàquilatoucheTheysayhewasveryfi
                    newhenhesaidthatheremarkedrepeatingthewordsinItalianDiomilhadatoGuaiachilatocchiGodhasgivenittomelethimwhotouchesitbewareIhopethiswill
                    provethelastdropthatwillmaketheglassrunoverAnnaPavlovnacontinuedThesovereignswillnotbeabletoendurethismanwhoisamenacetoeverythingThesover
                    eignsIdonotspeakofRussiasaidthevicomtepolitebuthopelessThesovereignsmadameWhathavetheydoneforLouisXVIIfortheQueenorforMadameElizabethNot
                    hingandhebecamemoreanimatedAndbelievemetheyarereapingtherewardoftheirbetrayaloftheBourboncauseThesovereignsWhytheyaresendingambassadorsto
                    complimenttheusurperAndsighingdisdainfullyheagainchangedhispositionPrinceHippolytewhohadbeengazingatthevicomteforsometimethroughhislorgne
                    ttesuddenlyturnedcompletelyroundtowardthelittleprincessandhavingaskedforaneedlebegantracingtheCondecoatofarmsonthetableHeexplainedthistoh
                    erwithasmuchgravityasifshehadaskedhimtodoitBâtondegueulesengrêledegueulesdazurmaisonCondesaidheTheprincesslistenedsmilingIfBuonaparterem
                    ainsonthethroneofFranceayearlongerthevicomtecontinuedwiththeairofamanwhoinamatterwithwhichheisbetteracquaintedthananyoneelsedoesnotlisten
                    toothersbutfollowsthecurrentofhisownthoughtsthingswillhavegonetoofarByintriguesviolenceexileandexecutionsFrenchsocietyImeangoodFrenchsoc
                    ietywillhavebeenforeverdestroyedandthenHeshruggedhisshouldersandspreadouthishandsPierrewishedtomakearemarkfortheconversationinterestedhi
                    mbutAnnaPavlovnawhohadhimunderobservationinterruptedTheEmperorAlexandersaidshewiththemelancholywhichalwaysaccompaniedanyreferenceofherst
                    otheImperialfamilyhasdeclaredthathewillleaveittotheFrenchpeoplethemselvestochoosetheirownformofgovernment;andIbelievethatoncefreefromtheu
                    surperthewholenationwillcertainlythrowitselfintothearmsofitsrightfulkingsheconcludedtryingtobeamiabletotheroyalistemigrantThatisdoubtfuls
                    aidPrinceAndrewMonsieurleVicomtequiterightlysupposesthatmattershavealreadygonetoofarIthinkitwillbedifficulttoreturntotheoldregimeFromwhat
                    IhaveheardsaidPierreblushingandbreakingintotheconversationalmostallthearistocracyhasalreadygoneovertoBonapartessideItistheBuonapartistswh
                    osaythatrepliedthevicomtewithoutlookingatPierreAtthepresenttimeitisdifficulttoknowtherealstateofFrenchpublicopinionBonapartehassaidsorema
                    rkedPrinceAndrewwithasarcasticsmileItwasevidentthathedidnotlikethevicomteandwasaiminghisremarksathimthoughwithoutlookingathimIshowedthem
                    thepathtoglorybuttheydidnotfollowitPrinceAndrewcontinuedafterashortsilenceagainquotingNapoleonswordsIopenedmyantechambersandtheycrowdedi
                    nIdonotknowhowfarhewasjustifiedinsayingsoNotintheleastrepliedthevicomteAfterthemurderoftheduceventhemostpartialceasedtoregardhimasaheroIf
                    tosomepeoplehewentonturningtoAnnaPavlovnaheeverwasaheroafterthemurderoftheductherewasonemartyrmoreinheavenandoneherolessonearthBeforeAnna
                    PavlovnaandtheothershadtimetosmiletheirappreciationofthevicomtesepigramPierreagainbrokeintotheconversationandthoughAnnaPavlovnafeltsurehe
                    wouldsaysomethinginappropriateshewasunabletostophimTheexecutionoftheDucdEnghiendeclaredMonsieurPierrewasapoliticalnecessityanditseemstome
                    thatNapoleonshowedgreatnessofsoulbynotfearingtotakeonhimselfthewholeresponsibilityofthatdeedDieuMonDieumutteredAnnaPavlovnainaterrifiedwh
                    isperWhatMonsieurPierreDoyouconsiderthatassassinationshowsgreatnessofsoulsaidthelittleprincesssmilinganddrawingherworknearertoherOhOhexcl
                    aimedseveralvoicesCapitalsaidPrinceHippolyteinEnglishandbeganslappinghiskneewiththepalmofhishandThevicomtemerelyshruggedhisshouldersPierr
                    elookedsolemnlyathisaudienceoverhisspectaclesandcontinuedIsaysohecontinueddesperatelybecausetheBourbonsfledfromtheRevolutionleavingthepeo
                    pletoanarchyandNapoleonaloneunderstoodtheRevolutionandquelleditandsoforthegeneralgoodhecouldnotstopshortforthesakeofonemanslifeWontyoucom
                    eovertotheothertablesuggestedAnnaPavlovnaButPierrecontinuedhisspeechwithoutheedingherNocriedhebecomingmoreandmoreeagerNapoleonisgreatbeca
                    seherosesuperiortotheRevolutionsuppresseditsabusespreservedallthatwasgoodinitequalityofcitizenshipandfreedomofspeechandofthepressandonlyf
                    orthatreasondidheobtainpowerYesifhavingobtainedpowerwithoutavailinghimselfofittocommitmurderhehadrestoredittotherightfulkingIshouldhaveca
                    lledhimagreatmanremarkedthevicomteHecouldnotdothatThepeopleonlygavehimpowerthathemightridthemoftheBourbonsandbecausetheysawthathewasagrea
                    tmanTheRevolutionwasagrandthingcontinuedMonsieurPierrebetrayingbythisdesperateandprovocativepropositionhisextremeyouthandhiswishtoexpress
                    allthatwasinhismindWhatRevolutionandregicideagrandthingWellafterthat
                    """
            , '\n');

    private ElsSequenceGenerator elsSequenceGenerator = new ElsSequenceGenerator();
    private int minInterval = 1;
    private int maxInterval = 100;

    @Test
    public void generateEquidistantLetterSequences() {
        final List<Els> actual = elsSequenceGenerator.generateEquidistantLetterSequences(minInterval, maxInterval, allLetters);

        final Els element0 = actual.get(0);
        final Els element1 = actual.get(1);
        final Els element2 = actual.get(2);
        final Els element3 = actual.get(3);
        final Els elementLast = CollectionUtils.lastElement(actual);

        assertAll(
                () -> assertEquals(allLetters, element0.getContent())
                , () -> assertEquals(1, element0.getFirstLetter())

                , () -> assertTrue(element1.getContent().startsWith("AdhtootikfhsaetoeyhcrntoaMlnseAnPvonadfhcmdoteepefeoaduclyntereiinbfr"))
                , () -> assertEquals(1, element1.getFirstLetter())

                , () -> assertTrue(element2.getContent().startsWith("nwadyuhnotiltscmdteooaintiaakdnaalvanoteoeyfhpoloGnanLcaaighipttoseo"))
                , () -> assertEquals(2, element2.getFirstLetter())

                , () -> assertTrue(element3.getContent().startsWith("AwtytnfiasodhoniainkAavvaohodfeoeGonuaygepiobo"))
                , () -> assertEquals(1, element3.getFirstLetter())

                , () -> assertTrue(elementLast.getContent().startsWith("GnryvafreaemltteauefrMtaPPgineocpttnaldomitwsdr"))
                , () -> assertEquals(100, elementLast.getFirstLetter())

                , () -> assertEquals((maxInterval * (maxInterval + 1)) / 2, actual.size())
        );
    }


}