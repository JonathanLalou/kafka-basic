package com.github.jonathanlalou.kafkabasic.batch;


import com.github.jonathanlalou.kafkabasic.domain.EquidistantLetterSequence;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EquidistantLetterSequenceGeneratorTaskletTest {

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

    private EquidistantLetterSequenceGeneratorTasklet equidistantLetterSequenceGeneratorTasklet = new EquidistantLetterSequenceGeneratorTasklet();
    private int minInterval = 1;
    private int maxInterval = 100;

    @BeforeEach
    void setUp() {
        equidistantLetterSequenceGeneratorTasklet.setMinInterval(minInterval);
        equidistantLetterSequenceGeneratorTasklet.setMaxInterval(maxInterval);
        equidistantLetterSequenceGeneratorTasklet.setAllLetters(allLetters);

    }

    @Test
    public void generateEquidistantLetterSequences() {
        final List<EquidistantLetterSequence> equidistantLetterSequences = equidistantLetterSequenceGeneratorTasklet.generateEquidistantLetterSequences(minInterval, maxInterval, allLetters);
        for (int i = 0; i < 10; i++) {
            System.out.println(ToStringBuilder.reflectionToString(equidistantLetterSequences.get(i)));
        }
        final EquidistantLetterSequence element0 = equidistantLetterSequences.get(0);
        final EquidistantLetterSequence element1 = equidistantLetterSequences.get(1);
        final EquidistantLetterSequence element2 = equidistantLetterSequences.get(2);
        final EquidistantLetterSequence element3 = equidistantLetterSequences.get(3);
        final EquidistantLetterSequence elementLast = equidistantLetterSequences.get(equidistantLetterSequences.size()-1);

        assertAll(
                () -> assertEquals(allLetters, element0.getContent())
                , () -> assertEquals(1, element0.getFirstLetter())

                , () -> assertTrue(element1.getContent().startsWith("AdhtootikfhsaetoeyhcrntoaMlnseAnPvonadfhcmdoteepefeoaduclyntereiinbfr"))
                , () -> assertEquals(1, element1.getFirstLetter())

                , () -> assertTrue(element2.getContent().startsWith("nwadyuhnotiltscmdteooaintiaakdnaalvanoteoeyfhpoloGnanLcaaighipttoseo"))
                , () -> assertEquals(2, element2.getFirstLetter())

                , () -> assertTrue(element3.getContent().startsWith("AwtytnfiasodhoniainkAavvaohodfeoeGonuaygepiobo"))
                , () -> assertEquals(1, element3.getFirstLetter())

                , () -> assertTrue(elementLast.getContent().startsWith("Annesou"))
                , () -> assertEquals(100, elementLast.getFirstLetter())
        );
    }


}