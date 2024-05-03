
import jFacehard.*;

// this file is meant to be a framework by which the old FACEHARD application (with its text interface)
// is to be delivered, using the transcoded jFacehard.Util, Impact, Projectile, and Armor classes

// ideally, the ugliness should mostly reside in here
public class FACEHARD {

    static Nation nation;

    static Projectile lastProj; // the previously-used projectile
    static Armor lastArmor; // the previously-used armor


    static Impact impact = new Impact();


    // 0 -> 6
    final static String[]  NBL_Str = {
            "N1> Navy BL w/o shatter, but all other damage & given AP cap =",
            "N2> Navy BL w/  shatter  and all other damage & given AP cap =",
            "(Worst (maximum) NBL at low OB; replaces N1 when OB <= 45 deg and N2 < N1)",
            "N3> Navy BL w/  shatter, but no  other damage & given AP cap =",
            "(Replaces N2 (& N1/N4 if N2 does) @ NBL or strikingVelocityFPS if no    shatter-changing damage)",
            "N4> Navy BL if unshattered/undeformed body and given AP cap  =",
            "(Best (minimum) NBL; replaces N1 @ NBL or strikingVelocityFPS if no penetration-changing damage)" };

    // 0 -> 8
    final static String[]  HBL_Str = {
            "H1> Holing BL without shatter using given AP cap             =",
            "(Unshattered HBL if < H2 and non-shatter damage reduces penetration @ HBL)",
            "(Includes Japanese uncapped Type 91 AP projectile w/cap head in place)",
            "H2> Holing BL with shatter (AP cap, if any, did not work)    =",
            "(Replaces H1 if H2 <= H1 (smaller hole) or other damage adds to shatter @ HBL)",
            "H3> Holing BL with shatter, but no other kind of damage      =",
            "(Best high-OB HBL; replaces H2 @ HBL or strikingVelocityFPS if no     shatter-changing damage)",
            "H4> Holing BL if unshattered/undeformed body & given AP cap  =",
            "(Best low -OB HBL; replaces H1 @ HBL or strikingVelocityFPS if no penetration-changing damage)" };

    final static String RESNOTE_Str = " BL USED FOR PENETRATION MARKED BY '-!-'/BL USED FOR POST-IMPACT LOGIC BY '-#-'";


    static String  ND_Str;
    static String  PAND_Str, FLAKE_Str, REMV_Str;
    static String  BDYDM1_Str, BDYDM2_Str, BDYDM3_Str, NSBRK1_Str, NSBRK2_Str, NSBRK3_Str, BD1_Str, BD2_Str;
    static String  BKPRT1_Str, BKPRT2_Str, BKPRT3_Str, BKPRT4_Str, BKPRT5_Str, EFFVEL_Str;
    static String  HBLTONBL_Str, PRJ_Str;
    static String  VELLTRU_Str, VELLSHAT_Str, VELLSHATMAX_Str, VELLND_Str, EFFPRINT1_Str;
    static String  VELHTRU_Str, VELHSHAT_Str, VELHSHATMAX_Str, VELHND_Str, EFFPRINT2_Str;
    static String  penetration_Str, WBL1_Str, WBL2_Str, WBL3_Str, WBL4_Str;
    static String  ONEPC_Str, RVU_Str, DPLG_Str, REMVEL_Str, BSNS1_Str, BSNS2_Str, BSNS3_Str;

    static String BCKPRNT_Str, TWOVEL_Str, NOPLG_Str;

    static String[] NOTE = new String[5]; // formerly NOTE1$ thru NOTE5$


    // formerly N1$ thru N4$, these contain some sort of state info on Naval and Holing BL's
    static String N1_Str, N2_Str, N3_Str, N4_Str;
    static String H1_Str, H2_Str, H3_Str, H4_Str;


    // FILEBEGIN MAIN

    // >>>>**** FACE-HARDENED ARMOR PENETRATION PROGRAM BY NATHAN OKUN ****<<<<
    //              >>>>**** VERSION 5.2 OF 7 JUNE 2002 ****<<<<
    //          >>>>**** MAIN PROGRAM MODULE "FH52MAIN.BAS" ****<<<<
    //



    static final int VERSION_INFO_PAGE_COUNT = 2;


    static Armor armor() { return impact.armor; }
    static Projectile proj() { return impact.proj; }

    // **** BEGIN ACTUAL PROGRAM CODE ****

    // I/O
    static void main(String args[]) {
        // *
        // **INITIALIZE PROGRAM FIRST TIME ONLY**
        boolean useSamePlate;
        boolean useSameProj;


        // PRINT SCRN HDR
        BASIC.CLS();
        BASIC.PRINTLN("NATHAN OKUN FACE HARDENED ARMOR PENETRATION PROGRAM(c) (VERSION 5.5(1/22/2004))");
        BASIC.PRINTLN();
        BASIC.PRINTLN(" *** Press RETURN/ENTER without entry to repeat a previous DATA VALUE entry ***");
        BASIC.PRINTLN();


        if (BASIC.askYesNo("Show DEFINITIONS and VERSION 5.2 Changes Information Screens?")) {
            int pageNumber;
            for (pageNumber = 0; pageNumber < VERSION_INFO_PAGE_COUNT;) {
                displayVersionPage(pageNumber + 1);
                if (BASIC.askYesNo("")) pageNumber++;
            }
        }

        BASIC.CLS();
        BASIC.PRINTLN();


        // conduct repeated data entries and firing trials until the user opts
        // out of the loop

        while (true) {



            useSameProj = BASIC.askYesNo("USE SAME PROJECTILE?");

            useSamePlate = BASIC.askYesNo("USE SAME ARMOR PLATE (RETAIN PARAMETER CHANGES, IF ANY)?");


            // input armor data, if not re-using previous run's plate
            // enter thickness and then type
            if (!useSamePlate) {

                int oldArmorType = armor() == null ? -1 : armor().type;
                int newArmorType;


                BASIC.PRINTLN("Select Face-Hardened Armor Plate Type:");

                for (int i=0; i< Armor.list.size(); i++) {
                    String leader = (i + 1 < 10) ? "   " : "  ";
                    BASIC.PRINTLN(leader + i + ". " + Armor.longNames[i +1]);
                }

                newArmorType = BASIC.askBoundedInt("", 1, Armor.list.size(), oldArmorType);

                if (newArmorType != oldArmorType) {

                    // SET NEW ARMOR VALUES & FLAGS
                    armor().initFromTemplate(newArmorType);
                }


                armor().inchesOfArmorPlating =
                        BASIC.askBoundedFloat("Armor plate thickness at impact center, in inches",
                                .1f, 100f, armor().inchesOfArmorPlating);


                //TYPE_GRUSON CHILLED CAST IRON & TERNI CEMENTED FACE THICKNESS PERCENT DECREASES
                //  WITH INCREASING PLATE THICKNESS  (ESTIMATES MADE AS TO CHANGES)
                displayGrusonAndTerniCementedDiscussion();


                BASIC.PRINTLN();

                if (armor().hasBeenEdited(lastArmor))
                    BASIC.PRINTLN("ARMOR HAS BEEN CHANGED FROM DEFAULT PARAMETERS.");

                editArmorParameters(); // ALLOW USER TO MODIFY ARMOR PARAMETERS
            }

            //CURVED-PLATE RULE DEFINED: CURVED FH PLATES ELIMINATE BODY DAMAGE TO STEEL PROJ AT obliquity > 45 DEG IF HBL <= VEL < NBL.
            //  IF SHATR OCCURS, RESULTS IN NOSE-ONLY SHATR INSTEAD OF USUAL COMPLETE SHATR.
            BASIC.PRINTLN();

            armor().isCurved = BASIC.askYesNo("Is plate strongly curved (part of dome, cylinder, ellipse, etc.)?");

            BASIC.PRINTLN();


            if (BASIC.askYesNo("Is there any backing material behind the face-hardened plate?")) {
                // BACKINGS ONLY INCREASE RESISTANCE, NOT PROJECTILE DAMAGE
                // INPUT THICKNESS OF WOOD, CEMENT, &/OR METAL BACK SUPPORT LAYERS BEHIND ARMOR PLATE
                inputArmorBackingInfo();
            } else
                armor().removeAllBacking();


            BASIC.PRINTLN();

            if (useSameProj) {
                // refurbish our existing projectile so it is ready for another firing
                proj().refurbishToFactoryCondition();
            } else {
                // else, if not re-using projectile from previous impact,
                // ask user to specify one

                // select a type of projectile
                selectProjectileTemplate();

                // then customize it as user desires
                inputProjectileDimensionsAndWeight();
            }



            impact.strikingVelocityFPS = BASIC.askBoundedInt("Striking velocity (VS), ft/sec (Integer only; Maximum=4000)",
                    1, 4000, impact.strikingVelocityFPS);

            // display DISCUSSION ON OBLIQUITY, SOFT AP CAPS, & HOODS
            displayObliquityAndCapsDiscussion();


            impact.obliquity =
                    BASIC.askBoundedFloat("Impact Obliquity (OB), in degrees",
                            0f, (impact.effectiveThicknessInProjectileDiameters() < armor().THIN() ? 80f : 75f), impact.obliquity);

            //  BEGIN NOSE COVERINGS LOST PRIOR TO FACE-HARDENED ARMOR IMPACT LOGIC *
            if (proj().bodyWeightInPounds == proj().originalWeight) {
                BASIC.PRINTLN("IF ANY, ALL NOSE COVERINGS WERE ALREADY REMOVED PRIOR TO IMPACT.");
                BASIC.PRINTLN();
            }


            impact.run();

            displayFinalResults();

            /*
            // generate hardcopy reports if desired
            if (jFacehard.BASIC.askYesNo("OUTPUT RESULTS TO PRINTER?")) {
                hardcopyPrintAll();
            }
            */

            // ask for another impact
            if (!BASIC.askYesNo("ANOTHER RUN?")) {
                break; // leave the loop of firing trials
            }

            BASIC.CLS();
            BASIC.PRINTLN();

            impact.prepForAnotherTest();

        } // end of firing impact loop

    } // main()



    static void selectProjectileTemplate() {
        Nation oldNation = nation;
        lastProj = proj();
        int i;


        BASIC.PRINTLN("Select projectile from the following table:"); BASIC.PRINTLN();
        BASIC.PRINTLN("NOTE: Many older guns kept old ammunition after the dates given below. The");
        BASIC.PRINTLN("dates are for projectiles introduced during that time period for any guns."); BASIC.PRINTLN();
        BASIC.PRINTLN("Projectile types marked by '(*)' are rough estimates based on little data.");
        BASIC.PRINTLN("When and if more information is discovered, these values may change.");
        BASIC.PRINTLN();

        for (i =0; i < Nation.list.size(); i++)
            BASIC.PRINTLN(BASIC.SPC(26) + (i+1) + ". " + Nation.list.elementAt(i).name());

        BASIC.PRINTLN();
        BASIC.PRINTLN("If the projectile you desire is not made by one of the above list or");
        BASIC.PRINTLN("it is not made to one of their designs, use FRANCE or ITALY as your selection");
        BASIC.PRINTLN("because both are mostly defaults.");
        BASIC.PRINTLN();

        int nationID = BASIC.askBoundedInt("", 1, Nation.list.size(), -1);

        if (nationID != -1)
            nation = Nation.list.elementAt(nationID);

        // OLD PROJECTILE SELECTION INVALID IF NATION CHANGES
        if (nation != oldNation)
            lastProj = null;


        do {
            BASIC.CLS();  BASIC.PRINTLN(); BASIC.PRINTLN("Available Projectile Types: ");

            for (i = 0; i < nation.projectiles.size(); i++) {
                BASIC.PRINTLN(" " + (i+1) + ". " + ((Projectile)nation.projectiles.elementAt(i)).name);
            }

            int idx = BASIC.askBoundedInt("Enter Projectile number", 1, nation.projectiles.size(), -1);

            if (idx != -1)
                impact.proj = nation.projectiles.elementAt(i);
        } while (proj() == null);
    }




    //************** DISPLAY FINAL RESULTS  ******************

        // was RESULTSPRINT
    static void displayFinalResults() {
        String AMPER_Str;

        // SET UP PAGE 2 EBL, NBL, & HBL VALUES & DEFINITION TEXT
        int noteFlag = displaySetupSecondPage();


        // *** PROJ BODY DAMAGE LOGIC IF IT FAILS TO PUNCH A HOLE ***
        // IF PROJ WILL BREAK ABOVE HBL THEN IT WILL CERTAINLY DO SO BELOW HBL
        if (impact.penetrationFlag == Impact.PENFLAG_NO_LARGE_HOLE && (impact.MINEV > impact.VHOL || noteFlag == 3 || impact.BRAIK)) {
            impact.BRK = 10; impact.BDYDM = 1;
        }


        //**** SET UP PROJ, PLATE, & IMPACT DATA & DESCRIPTION OF RESULTS ****

        ND_Str = (impact.SHAT ? "shattered" : "deformed or broken");
        REMVEL_Str = "Proj Remaining Velocity: "; ONEPC_Str = ""; VTOTAL = 0;

        BD1_Str = (impact.SHAT ? " P" : " If nose breaks, p");
        if (impact.SHAT) {
            BD2_Str = " at least ";
            if (impact.BRK == 0)
                BD2_Str = " "; // NOSE-ONLY SHATR
        } else
            BD2_Str = " up to ";

        BDYDM1_Str = ""; BDYDM2_Str = ""; BDYDM3_Str = "";
        NSBRK1_Str = ""; NSBRK2_Str = ""; NSBRK3_Str = ""; FLAKE_Str = ""; NOPLG_Str = "";
        WBL1_Str = ""; WBL2_Str = ""; WBL3_Str = ""; WBL4_Str = ""; TWOVEL_Str = ""; AMPER_Str = "";
        BSNS1_Str = ""; BSNS2_Str = ""; BSNS3_Str = ""; DPLG_Str = ""; RVU_Str = "";


        //**** BEGIN 1ST DISPLAY ****

        //PRINT WHAT HAPPENED TO PLATE AND PROJECTILE
        displayPenetrationDescription(); //impact.penetrationType);


        displayNoseBodyDamage();


        BASIC.PRINTLN("Plate Type = " + armor().type);
        BASIC.PRINTLN("Plate Thickness, inches: Actual = " +
                Util.round(armor().inchesOfArmorPlating, .01f) + " & Effective ('Q'+backing) = " +
                Util.round(armor().totalEffectiveInchesWithBacking(), .01f));
        BASIC.PRINTLN("Backing Thickness, inches: ");
        if (armor().totalActualInchesOfBacking()  == 0)
            BASIC.PRINTLN("NONE USED.");
        else {
            if (armor().inchesOfWoodBacking > 0) {
                BASIC.PRINTLN("WOOD = " + armor().inchesOfWoodBacking);
                AMPER_Str = "& ";
            }
            if (armor().inchesOfCementBacking > 0) {
                BASIC.PRINTLN("CEMENT = " + armor().inchesOfCementBacking);
                AMPER_Str = "& ";
            }
            if (armor().getInchesOfMetalBacking() > 0) {
                String S_Str = ""; if (armor().getNumberMetalBackingPlates() > 1) S_Str = "s";
                String PLTNMBR_Str = "" + armor().getNumberMetalBackingPlates());
                String MTLPRNT_Str = " (" + PLTNMBR_Str + " plate" + S_Str + ")";
                BASIC.PRINTLN(AMPER_Str + "METAL =" + armor().getInchesOfMetalBacking() + MTLPRNT_Str);
                switch (armor().getMetalBackingType()) {
                    case Armor.BACKING_WROUGHT:
                    BCKPRNT_Str = BKPRT1_Str;
                    break;
                    case Armor.BACKING_MEDIUM_STEEL:
                    BCKPRNT_Str = BKPRT2_Str;
                    break;
                    case Armor.BACKING_HIGH_TENSILE_STEEL:
                    BCKPRNT_Str = BKPRT3_Str;
                    break;
                    case Armor.BACKING_IMPROVED_HIGH_TENSILE_STEEL:
                    BCKPRNT_Str = BKPRT4_Str;
                    break;
                    case Armor.BACKING_SPECIAL_TREATMENT:
                    BCKPRNT_Str = BKPRT5_Str;
                    break;
                }
                BASIC.PRINTLN("*TYPE: " + BCKPRNT_Str);
            } else
                BASIC.PRINTLN();
        }


        //PRINT PROJ IMPACT CONDITIONS & EXIT ANGLE
        displayImpactInformation();


        if (impact.penetrationFlag == Impact.PENFLAG_COMPLETE)
            displayPostImpactMotionEffects();



        NPWPRNT = INT(10 * impact.NORMPLUGWT + .5) / 10f;
        DPWPRNT = INT(10 * impact.DELTAPLUGWT + .5) / 10f;
        if (impact.NORMPLUGWT <= .05) {
            NOPLG_Str = "No plugs ejected from plate.";
            BASIC.PRINTLN(NOPLG_Str);
        } else {
            BASIC.PRINTLN("Plug Weights, pounds: Normal = " + NPWPRNT + " & Delta = " + DPWPRNT);
        }

        //PRINT MSG ABOUT SPLINTERS THROWN FROM PLATE BACK BELOW HBL
        displaySplinterReport();

        if (impact.NORMPLUGWT > .05f)
            BASIC.PRINTLN("Normal Plug Velocity = " +  VNPLUG + " ft/sec");

        if (impact.BDYDM > 0) {
            TWOVEL_Str = "IF PROJECTILE BODY BREAKS, ASSUME 50% OF BODY WEIGHT IN UPPER AND LOWER HALVES.";
            BASIC.PRINTLN(TWOVEL_Str);
        }

        //PROJ REMAINING VELOCITY PRINTOUT
        displayRemainingVelocityMessages();



        String title = "CALCULATED HOLING, NAVY, AND EFFECTIVE BALLISTIC LIMITS";


        // **** DISPLAY SECOND PAGE WITH B.L. DETAILS IF SELECTED BY USER ****

        if (BASIC.askYesNo("DISPLAY " + title))
            displaySecondPage(title);

    }


    //* MAX PRINTOUT VEL LOGIC *
    static String FOURKCHK(float vel) {
        return (vel > 4000 ? ">4000 ft/sec" : ("" + vel + " ft/sec"));
    }


    //
    //* ALLOW USER TO CHANGE PLATE DATA
    static void editArmorParameters() {
        BASIC.PRINTLN();

        if (!BASIC.askYesNo("Do you want to modify armor plate's parameters?"))
            return;

        displayNumericValueLegend();
        displayOriginalArmor();

        if (BASIC.askYesNo("Restore all original armor values?"))
            armor().initFromTemplate(armor().type);



        if (BASIC.askYesNo("Change any armor parameters?")) {

            BASIC.PRINTLN("EFFECT OF 'UB' VALUE ONLY CHANGES WHEN 'UB' > 30, 52, 62, 67.5, 75, OR 90.");
            BASIC.PRINTLN("MINIMUM 'Q' & 'QDAM' IS 0.1 AND MAXIMUM 'QDAM' IS 'Q' (USUAL VALUE).");
            BASIC.PRINTLN();

            int ubEntry;

            BASIC.PRINT("UB:       ");
            ubEntry = BASIC.askBoundedInt("", 0, 100, armor().UB);  // TODO: check Impact if this was UBMAN

            if (armor().type == Armor.TYPE_GRUSON || armor().type == Armor.TYPE_TC) {
                if (ubEntry != armor().UB && ubEntry == armor().calculatedUB())
                    BASIC.PRINTLN(" *** TYPE_GRUSON/TC ARMORS:  Calculated Default UB Value Restored");

                armor().UBMAN = ubEntry;
            }
            armor().UB = ubEntry;

            BASIC.PRINT("Q:        ");
            armor().quality = BASIC.askBoundedFloat("", .1f, 1f, armor().quality);

            BASIC.PRINT("QDAM:     ");
            armor().shellShatteringQuality = BASIC.askBoundedFloat("", .1f, armor().quality, armor().shellShatteringQuality);

            BASIC.PRINT("CARTWL:   ");
            armor().CARTWL = BASIC.askBoundedInt("", Armor.CARTWHEEL_BACKSPALL_NO, Armor.CARTWHEEL_BACKSPALL_RESISTANCE, armor().CARTWL);

            BASIC.PRINT("CMPND:    ");
            armor().isCompound = BASIC.askYesNoDefault("",  armor().isCompound);

            BASIC.PRINT("THNCHL:   ");
            armor().isThinlyFaced = BASIC.askYesNoDefault("",  armor().isThinlyFaced);

            BASIC.PRINT("SOFTSHAT: ");
            armor().SOFTSHAT = BASIC.askBoundedInt("", Armor.SHATTER_NO_PROJ, Armor.SHATTER_WEAK_SOFTCAPPED_PROJ,  armor().SOFTSHAT);

            BASIC.PRINT("THKTHN:   ");
            armor().THKTHN= BASIC.askBoundedInt("", Armor.THKTHN_BRITTLE, Armor.THKTHN_FOOBAR, armor().THKTHN);
            BASIC.PRINTLN();
        } else
            BASIC.PRINTLN(); // NO CHANGES

    } // editArmorParameters



    // returns a flag reporting the type of note made
    static int displaySetupSecondPage() {
        int noteFlag = 0;

        String fooster = "CHECK FOOSTER STRING";

        VELLTRU_Str = FOURKCHK(impact.VLTRU);
        VELLSHAT_Str =   FOURKCHK(impact.VLSHAT);
        VELLSHATMAX_Str = FOURKCHK(impact.VLSHATMAX);
        VELLND_Str = FOURKCHK(impact.VLND);

        VELHTRU_Str = FOURKCHK(impact.VHTRU);
        VELHSHAT_Str = FOURKCHK(impact.VHSHAT);
        VELHSHATMAX_Str = FOURKCHK(impact.VHSHATMAX);
        VELHND_Str = FOURKCHK(impact.VHND);

        //**** CALCULATE 'EFFECTIVE' LIMIT 'MINEV' FOR THIS IMPACT FROM ALL DATA ****

        // INIT VARIABLES

        PAND_Str = HBLTONBL_Str = EFFPRINT1_Str = EFFPRINT2_Str = NTSTV_Str = "";

        for (int i = 0; i < NOTE.length; i++) NOTE[i] = "";

        EFFVEL_Str = "";

        // INIT EFF VEL CALC VARIABLES
        impact.initializeEffectiveVelocityVariables();


        if (impact.SHAT) {
            //SHATRD PROJ
            // IF NOSE-ONLY SHATR W/INTACT BODY WHEN VEL > HBL
            impact.MINEV1 = impact.VHOL;
            noteFlag = 1;
        }

        if (impact.NSSHAT == 2)
            noteFlag = 0; //  NOSE-ONLY SHTR DUE TO LOW IMPACT VEL
        else if (impact.NSSHAT == 0 || (impact.NSSHAT > 0 && (proj().LTCASE > Projectile.LTCASE_HEAVYCASE))) {
            noteFlag = 0; // COMPLETE SHATR OR ANY SHATR OF LARGE-CAVITY (LIGHTCASE) PROJ
            impact.MINEV1 = -1; //  SPECIAL FLAG FOR CURVED-PLATE RULE
        } else if (impact.NSSHAT > 0 && impact.BRAIK) {
                //WEAK-BODIED STEEL PROJ BREAK IF 'strikingVelocityFPS' < 'VLMT' FOR STEEL ARMOR OR 'strikingVelocityFPS' < 'VHOL'
                //  IN TYPE_COMPOUND ARMOR.  CHILLED CAST IRON PROJ ON TYPE_COMPOUND ARMOR ACTS AS STEEL-ON-STEEL HERE.
            if (armor().isCompound && proj().SHATRES != Projectile.SHATRES_VERYLOW) {
                noteFlag = 0;
            } else {
                impact.MINEV3 = impact.VLMT;
                impact.MINEV1 = -1; //  SPECIAL FLAG FOR CURVED-PLATE RULE
            }
        }
        else
        //NO PROJ SHATR
        if (proj().LTCASE > Projectile.LTCASE_HEAVYCASE) {
            if (proj().LTCASE == Projectile.LTCASE_MEDIUMCAVITY) {
                //LIGHTCASE = 2 NOSE DAMAGE INCLUDES CAVITY, CHANGING CRITICAL VELOCITY CHECK
                float f = impact.NSCRITICALV();
                MTMP = (int)f;
                if (MTMP == f)
                    impact.MINEV2 = MTMP;
                else
                    impact.MINEV2 = MTMP + 1;

            } else
                //LIGHTCASE = 1 NOSE DAMAGE EFFECTS CAVITY TOO (4-7% CAVITY SIZES, EVEN IF NOT FULL)
            impact.MINEV2 = impact.VLMT;

        }

        if (impact.VITRU == -1 && impact.CRTAPR > 0 && impact.NSSHAT == 0) {

                //CRITICAL VEL BODY DAMAGE TEST (SKIP IF NOSE-ONLY SHATR)
                //
            float critVel = impact.APCRITICALV();
            MTMPCR = (int)critVel;
            if (MTMPCR < critVel)
                MTMPCR += 1; //  ROUND TO NEAREST WHOLE VEL


                //
                // GET NOSE BROKEN MIN VEL FOR CRIT VEL USE, IF NEEDED

            float f = impact.NSCRITICALV();
            MTMPNS = (int)f;
            if (MTMPNS < f)
                MTMPNS += 1; //  ROUND TO  NEAREST WHOLE VEL     // ask Impact

            NTSTV_Str = "" + MTMPNS; //  NEED NOSE DAM LIMIT STRING FOR 'NOTE[4]'

            //
            // CRIT VEL TEST ONLY MADE IF NOSE BREAKS OR PROJ COMPLETELY PENETRATES
            if (MTMPNS >= impact.VLMT)
                MTMP = MTMPCR; //  BODY DAMAGE THRESHOLD IS MINIMUM EFF VEL
            else if (MTMPNS < impact.VLMT && MTMPCR <= MTMPNS)
                MTMP = MTMPCR; //  BODY DAMAGE THRESHOLD IS MINIMUM EFF VEL
            else if (MTMPNS < impact.VLMT && MTMPCR > MTMPNS && MTMPCR < impact.VLMT)
                MTMP = MTMPNS; //  NOSE DAMAGE THRESHOLD IS MINIMUM EFF VEL
            else {
                MTMP = MTMPCR; //  BODY DAMAGE THRESHOLD IS MINIMUM EFF VEL
                NOTE[4] = "NEITHER NOSE NOR BODY DAMAGE OCCURS BETWEEN" + NTSTV_Str + " FT/SEC AND NBL";
            }
            impact.MINEV3 = MTMP;
            if (NOTE[4].equals("") && impact.VLMT == impact.VLND && !impact.BRAIK && impact.MINEV3 < impact.VLMT) {
                impact.MINEV3 = impact.VHOL;
                if (proj().SHATRES != Projectile.SHATRES_HIGH)
                    NOTE[4] = "IF NO SHATTER, MINOR PROJ DAMAGE IF HBL <= strikingVelocityFPS < NBL; IF strikingVelocityFPS < HBL BREAKAGE LIKELY"
                else
                    NOTE[4] = "IF NO SHATTER, USUALLY MINOR PROJ DAMAGE IF strikingVelocityFPS < NBL, WHETHER PLATE HOLED OR NOT"

            }
        } else if (impact.VITRU > -1) {
            impact.MINEV3 = impact.VITRU; // PROJ USES 'QE' FORMULA
            if (impact.VITRU == impact.VLND && !impact.BRAIK) {
                impact.MINEV3 = impact.VHOL;
                if (proj().SHATRES != Projectile.SHATRES_HIGH)
                    NOTE[4] = "IF NO SHATTER, MINOR PROJ DAMAGE IF HBL <= strikingVelocityFPS < NBL; IF strikingVelocityFPS < HBL BREAKAGE LIKELY";
                else
                    NOTE[4] = "IF NO SHATTER, USUALLY MINOR PROJ DAMAGE IF strikingVelocityFPS < NBL, WHETHER PLATE HOLED OR NOT";

            }
        } else if (proj().canBend && impact.obliquity >= impact.criticalObliquity) {
                // PROJ USES BRITISH DEFORMING PROJ RULES
            if (proj().CARDONALD == Projectile.CARDONALD_CARDONALD) {
                impact.MINEV3 = impact.VSCRIT;
                NOTE[4] = "BRITISH DEFORMING CARDONALD PROJECTILE";
            } else {
                impact.MINEV3 = -1; //  SPECIAL FLAG FOR CURVED-PLATE RULE
                NOTE[4] = "BRITISH DEFORMING NON-CARDONALD PROJECTILE";
            }
        } // IF NONE OF THE ABOVE, THEN 'impact.MINEV3 = 0'

        if (impact.obliquity > armor().OBRK()) {
            // 'OBRK' IS MAX OB TO REMAIN EFFECTIVE IF 'strikingVelocityFPS' < 'VLMT' UNLESS STEEL PROJ
            // HITS CURVED ARMOR, WHICH HAS SPECIAL LOGIC
            if (!impact.curveFlag)
                impact.MINEV4 = impact.VLMT;
        }
        if (impact.BRAIK) {
            //WEAK-BODIED STEEL PROJ BREAK IF 'strikingVelocityFPS' < 'VLMT' FOR STEEL ARMOR OR 'strikingVelocityFPS' < 'VHOL'
            //  IN TYPE_COMPOUND ARMOR.  CHILLED CAST IRON PROJ ON TYPE_COMPOUND ARMOR ACTS AS STEEL-ON-STEEL HERE.
            if (armor().isCompound && proj().SHATRES < Projectile.SHATRES_VERYLOW)
                impact.MINEV5 = impact.VHOL;
            else
                impact.MINEV5 = impact.VLMT;
        }
        END IF

        //SORT OUT TRUE MIN EFF VEL

        if (impact.MINEV1 >= 0 && impact.MINEV < impact.MINEV1)
            impact.MINEV = impact.MINEV1;
        if (impact.MINEV < impact.MINEV2)
            impact.MINEV = impact.MINEV2;
        if (impact.MINEV3 > 0 && impact.MINEV < impact.MINEV3)
            impact.MINEV = impact.MINEV3;
        if (impact.MINEV < impact.MINEV4)
            impact.MINEV = impact.MINEV4;
        if (impact.MINEV < impact.MINEV5)
            impact.MINEV = impact.MINEV5;


            // LIMIT MINIMUM EFFECTIVE BL TO 4000 FT/SEC MAXIMUM
     fooster = FOURKCHK(impact.MINEV);


        // **** DETERMINE DAMAGE TO PLATE & PROJ USING VARIOUS B.L. VALUES ****



      if (impact.curveFlag && (impact.MINEV <= 0 || impact.MINEV >= impact.VHOL)) {
      //NOTE: IF NOT TRUE, THEN PROJ ALREADY EFFECTIVE WHEN CURVED-PLATE RULE BEGINS AT HBL (SKIP LOGIC)
      //CURVED-PLATE RULE APPLIED
      if (impact.MINEV == 0 || impact.MINEV1 == -1 || impact.MINEV3 == -1) {
          impact.MINEV = 0;
          noteFlag = 2; // REGULAR CURVED-PLATE RULE FORCES NOSE-ONLY SHATR
      } else if (impact.MINEV > impact.VHOL && impact.MINEV <= impact.VLMT) {
          noteFlag = 3; // CURVED-PLATE RULE LOWERS EFFECTIVE LIMIT TO HBL
      } else if (impact.MINEV > impact.VLMT)
          noteFlag = 4; // GAP EXISTS BETWEEN EFFECTIVE LIMIT && CURVED-PLATE RULE VEL REGION

      }

      if (proj().LTCASE > Projectile.LTCASE_HEAVYCASE) {
          //LARGE-CAVITY PROJ
          if (impact.SHAT) {
              EFFPRINT1_Str = " NEVER (NOSE SHATTER REACHES CAVITY)";

              displaySetTextForSoftShatRule(noteFlag); // NOSE-ONLY SHATR DOES NOT PREVENT CAVITY DAMAGE OF A LIGHTCASE PROJ
              return noteFlag;
          }
          if (impact.MINEV2 > 0 && impact.MINEV = impact.MINEV2) {
              NSFLG = 1; noteFlag = 0; // ALL NOSE BREAKAGE OF A LIGHTCASE PROJ CAUSES LOSS OF EFFECTIVENESS
          }
      }

      //EFFECTIVE PROJ
      //
      if (noteFlag >= 2) {
              //CURVED-PLATE RULE
          if (noteFlag == 4) {
              EFFVEL_Str = fooster;
              if (impact.VHOL <= 4000)
                  PAND_Str = " *AND*";
          }
          if ((noteFlag == 2 || noteFlag == 4) && impact.VHOL <= 4000) {
              HBLTONBL_Str = " BETWEEN HOLING BL & NAVY BL (SEE BELOW)";
              displaySetTextForCurvedPlateRule();
              return noteFlag;
          }
      }

      if (noteFlag == 3)
          impact.MINEV = impact.VHOL; // MIN EFFECTIVE LIMIT LOWERED TO HBL BY CURVED-PLATE RULE
      else {
          if (impact.MINEV == 0 && impact.MINEV1 != -1 && impact.MINEV3 != -1) {
              EFFPRINT1_Str = " USUALLY EFFECTIVE (CAVITY IMMUNE TO NOSE DAMAGE)";
              displaySetTextForSoftShatRule(noteFlag);
              return noteFlag;
          }


          if (impact.MINEV == 0 && impact.MINEV1 == -1) {
              EFFPRINT1_Str = " NEVER (COMPLETE SHATTER)";
              return noteFlag; // EXIT
          }

          if (impact.MINEV3 == -1) {
              NVRFLAG = 1;
              EFFPRINT1_Str = " RARELY.  EXCEEDS BREAKAGE ANGLE (DEGREES):";
              return noteFlag;
          }

          if (proj().HARD == -1 && proj().SHATRES == Projectile.SHATRES_LOW && impact.MINEV <= impact.VLMT) {
              EFFPRINT1_Str = " NAVY BL";
              NVRFLAG = 0;
              NSFLG = 1;
          } else if (impact.MINEV != impact.VLMT && impact.MINEV > impact.VHOL)
            EFFVEL_Str = fooster;
          else
          if (impact.MINEV == impact.VLMT)
              EFFPRINT1_Str = " NAVY BL";
          else {
              EFFPRINT1_Str = " HOLING BL";
              if (noteFlag == 3) {
                  EFFPRINT2_Str = " (DUE TO CURVED PLATE) (SEE BELOW)";
                  displaySetTextForCurvedPlateRule();
                  return  noteFlag;
              }
          }
      }

      if (NVRFLAG == 0 && NSFLG == 1)
          EFFPRINT2_Str = " (NOSE DAMAGE REACHES CAVITY)";

      displaySetTextForSoftShatRule(noteFlag);

        return noteFlag;
    } // displaySetUpSecondPage


    private static void displaySetTextForCurvedPlateRule() {
            // PRINT OUT CURVE PLATE RULE ON SCREEN OR ON PAPER WHEN IT APPLIES

         NOTE[0] = "SPECIAL CURVED-PLATE RULE FOR BODY DAMAGE:";
         NOTE[1] = "  Projectiles remain effective if they are steel & hit curved plates at over 45";
         NOTE[2] = "  degrees obliquity with Striking Velocities between plate Holing BL & Navy BL.";

         if (impact.SHAT)
           NOTE[3] = "  Complete shatter of this projectile will occur otherwise.";
         else if (proj().canBend && impact.obliquity >= impact.criticalObliquity && proj().CARDONALD == Projectile.CARDONALD_NO)
           NOTE[3] = "  Complete breakup of this projectile will occur otherwise.";
         else
         if (impact.MINEV == impact.VHOL)
             NOTE[3] = "  Other effects keep projectile effective above Navy BL.";
    }



    private static void displaySetTextForSoftShatRule(int noteFlag) {
        if (noteFlag == 1) {
          NOTE[0] = "SPECIAL HOOD IF strikingVelocityFPS>=NBL OR SOFT CAP & EXTRA-TOUGH PLATE NOSE-ONLY SHATTER RULE:";
          NOTE[1] = "  Nose-only shatter usually occurs if OB<=15 deg and sometimes if 15<OB<20 deg.";
          NOTE[2] = "  Complete shatter of this projectile against this armor will occur otherwise.";
        }
    }

    //*** END OF PROGRAM CODE FOR MAIN MODULE "FH40MAIN.BAS" ***

// FILEBEGIN 1
        // >>>>**** FACE-HARDENED ARMOR PENETRATION PROGRAM BY NATHAN OKUN ****<<<<
        //               >>>>**** VERSION 5.2 OF 7 JUNE 2002 ****<<<<
        //      >>>>**** 1ST PRINT & SUBPROGRAM MODULE "FH52SBM1.BAS" ****<<<<
        //
        //  THE FOLLOWING MUST BE IDENTICAL AT TOP OF ALL MODULES OF THIS PROGRAM !!
        //
               //
        // ** END OF MODULE-LEVEL "FH40SBM1.BAS" CODE **




        //
        // INPUT WOOD, CEMENT, &/OR METAL BACK SUPPORT LAYER THICKNESS BEHIND ARMOR
        //
        static void  inputArmorBackingInfo() {

            float input = -1f;


            BASIC.PRINTLN();
            BASIC.PRINTLN("NOTE: if there is no backing of a particular type, enter '0'");
            BASIC.PRINTLN();

            armor().inchesOfWoodBacking =
                    BASIC.askBoundedFloat("Thickness of wood backing, in inches", 0f, 100f, armor().inchesOfWoodBacking);


            armor().inchesOfCementBacking =
                    BASIC.askBoundedFloat("Thickness of cement-type backing, in inches", 0f, 100f, armor().inchesOfCementBacking);


            float inches = armor().getInchesOfMetalBacking();
            int   layers = armor().getNumberMetalBackingPlates();
            int metalType = armor().getMetalBackingType();


            inches = BASIC.askBoundedFloat("Thickness of all metal backing, inches", 0f, 100f, inches);


            if (inches > 0) {
                if (layers <= 0)
                    layers = 1;

                for (int i = Armor.BACKING_WROUGHT; i <= Armor.BACKING_SPECIAL_TREATMENT; i++)
                    BASIC.PRINTLN(" " + i + ". " + Armor.metalBackingTypeName(i));

                BASIC.PRINTLN();

                metalType = BASIC.askBoundedInt("Enter number of desired backing type", 1, Armor.NUM_BACKING_TYPES, metalType);
                BASIC.PRINTLN();


                layers = BASIC.askBoundedInt("Number of (laminated) metal backing plates", 1, 100, layers);

            } else
                BASIC.PRINTLN("NO METAL BACKING");

            armor().setMetalBacking(metalType, layers, inches);


        } // inputArmorBackingInfo




        // display TYPE_GRUSON (ARMOR = 1) OR TERNI CEMENTED (ARMOR = 12) ARMOR FACE THICKNESS LOGIC
        //
        static void  displayGrusonAndTerniCementedDiscussion() {

            if (armor().type == Armor.TYPE_GRUSON || armor().type == Armor.TYPE_TC) {
                BASIC.PRINTLN();
                if (armor().type == Armor.TYPE_GRUSON) {
                    BASIC.PRINTLN("Gruson Armor Back Layer Percent (UB) varies linearly (estimate) from a minimum");
                    BASIC.PRINTLN("of 45% if TA <= 15.75 inches to a maximum of 67% if TA >= 33.07 inches.");
                } else  {
                    BASIC.PRINTLN("Terni Cemented Back Layer Percent (UB) varies step-wise (estimate) as follows:");
                    BASIC.PRINTLN("If Actual Plate Thickness (TA)<=5.5 inches, UB=50; if 5.5<TA<=6.5, then UB=55;");
                    BASIC.PRINTLN("if 6.5<TA<=7.5, then UB=60; if 7.5<TA<=10, then UB=65; & if TA>10.5, then UB=70");
                }
                BASIC.PRINTLN("  CURRENT PLATE BACK LAYER FROM 'UB' DEFAULT CALCULATION (%) = " + armor().calculatedUB());
                PRINT("  CURRENT MANUAL 'UB' OVERRIDE VALUE (%) = ");
                if (armor().UBMAN != armor().UBCALC)
                    BASIC.PRINTLN("" + armor().UB);
                else
                    BASIC.PRINTLN(" NONE (Default value in use)");
            }

        }  // displayGrusonAndTerniCementedDiscussion



            // DISPLAY RESULTS OF PROJECTILE NOSE AND BODY DAMAGE COMPUTATIONS
        static void displayNoseBodyDamage() {

            switch (impact.BDYDM) {
                case 0:
                    if (impact.penetrationFlag == PENFLAG_COMPLETE) {
                        if (impact.NSBRK > 0) {
                            BDYDM1_Str = "Projectile nose damaged by impact, but lower body undamaged.";
                            BDYDM2_Str = " Projectile still 'effective' unless nose damage reaches explosive cavity.";
                        } else {
                            BDYDM1_Str = "Neither projectile nose nor lower body damaged by impact.";
                            BDYDM2_Str = " Projectile still 'effective' and intact other than losing nose coverings.";
                        }
                    } else {
                        if (impact.NSBRK > 0)
                            BDYDM1_Str = "Projectile nose damaged so 50% chance that only minor lower body damage occurs.";
                        else
                            BDYDM1_Str = "Projectile nose intact so only minor lower body damage occurs most of the time.";

                        BDYDM2_Str = " Projectile usually will still be 'effective' if lower body damage is minor.";
                    }
                    break;
                case 1:
                    if (impact.NSBRK == 0)
                        BDYDM1_Str = "Projectile lower body damaged by impact.";
                    else
                        BDYDM1_Str = "Projectile nose and lower body damaged by impact.";

                    if ((proj().SHATRES != Projectile.SHATRES_HIGH && !armor().isCompound) ||
                        (proj().SHATRES == Projectile.SHATRES_VERYLOW && armor().isCompound))
                        BDYDM2_Str = " Projectile is not 'effective' and lower body is always broken up by impact.";
                    else
                        BDYDM2_Str = " Projectile is not 'effective' and lower body is usually deformed or broken.";

                    break;
                case 2:
                    BDYDM1_Str = "Projectile nose " + ND_Str + " and lower body damaged by impact.";
                    BDYDM2_Str = BD1_Str + "Projectile loses" + BD2_Str + "33% of body weight & is much weaker.";
                    BDYDM3_Str = " Projectile is not 'effective' and lower body is always broken up by impact.";
                    break;
            }

            BASIC.PRINTLN(BDYDM1_Str);
            BASIC.PRINTLN(BDYDM2_Str);

            if (impact.BDYDM == 2) {
                //NOSE ALREADY DESTROYED BY IMPACT
                BASIC.PRINTLN(BDYDM3_Str);
            } else {
                //CHECK FOR NOSE DAMAGE ('BDYDM3_Str' = "")
                if (impact.NSBRK == 0)
                    NSBRK1_Str = "Projectile nose suffers no major damage but all nose coverings stripped off.";
                else {
                    NSBRK1_Str = "Projectile nose " + ND_Str + " by impact.";
                    NSBRK2_Str = BD1_Str + "rojectile loses" + BD2_Str + "33% of body weight & is much weaker.";
                    if (proj().LTCASE > Projectile.LTCASE_HEAVYCASE)
                        NSBRK3_Str = " Projectile's large explosive cavity is rendered 'ineffective' by nose damage.";
                    else
                        NSBRK3_Str = " Projectile's small explosive cavity usually not affected by just nose damage.";

                }
                if ((proj().LTCASE < Projectile.LTCASE_MEDIUMCAVITY) || (NSBRK3_Str.equals(""))) {
                    BASIC.PRINTLN(NSBRK1_Str);
                    if (!NSBRK2_Str.equals("")) {
                        BASIC.PRINTLN(NSBRK2_Str);
                        BASIC.PRINTLN(NSBRK3_Str);
                    }
                }
            }

        } //   displayNoseBodyDamage


        static void displayNumericValueLegend() {
            BASIC.PRINTLN("NUMERICAL VALUES:");
            BASIC.PRINTLN(" 'UB'   = Percent Unhardened Back Layer (Scaling Factor step function logic)");
            BASIC.PRINTLN(" 'armor().quality'    = Relative Armor Resistance Quality (compared to best WWII armors)");
            BASIC.PRINTLN(" 'armor().shellShatteringQuality' = Relative Armor Projectile Damaging Ability (ditto)");
            BASIC.PRINTLN("**Minimum value for 'armor().quality' & 'armor().shellShatteringQuality' = 0.1.  Also, 'armor().shellShatteringQuality' can never exceed 'armor().quality'.**");
            BASIC.PRINTLN("FLAGS:");
            BASIC.PRINTLN(" 'CARTWL'   = If '1', brittle armor ejects huge plugs; if '2', at high OB only");
            BASIC.PRINTLN(" 'isCompound'    = Compound Armor (never shatters steel projectiles)");
            BASIC.PRINTLN(" 'isThinlyFaced'   = Back layer over 75% of plate (reduced damage-causing ability)");
            BASIC.PRINTLN(" 'SOFTSHAT' = Shatter soft-capped proj (='1'); except Midvale Unbreakable (='2')");
            BASIC.PRINTLN(" 'THKTHN'   = Ductility lowers THIN boundary from .55 cal(0) to .45(2) or .35(1)");
            BASIC.PRINTLN("NOTE:  SEE PROGRAM LISTING OR DESCRIPTION DOCUMENT FOR DETAILS.");

        } // displayNumericValueLegend


        static void displayObliquityAndCapsDiscussion() {
            // PRINT DISCUSSION ABOUT OBLIQUITY, HOODS, & SOFT AP CAPS

            BASIC.PRINTLN();
            PRINT("  OBLIQUITY IS MEASURED SO THAT ZERO DEGREES IS AT RIGHT-ANGLES TO PLATE FACE.");
            PRINT("  MAXIMUM OBLIQUITY FOR PLATE DAMAGE IS 75 DEG. (THICK) & 80 DEG. (OTHERWISE).");
            PRINT("  MAXIMUM OBLIQUITY FOR COMPLETE PENETRATION WITHOUT SHATTER IS 70 DEG.");
            if (proj().softCapOrHood()) {
                //SOFT AP CAP OR HOOD EXISTS
                if (proj().HARD == -1) {
                    PRINT("HOODS USUALLY ACT AS SOFT AP CAPS ABOVE THE PLATE'S NAVY BALLISTIC LIMIT ONLY.");
                    PRINT("BELOW THE NBL, HOODED NOSES USUALLY BREAK UP; INTO EXPLOSIVE CAVITY IF OVER 3.5%.");
                }
                BASIC.PRINTLN("SOFT AP CAPS ALWAYS & HOODS USUALLY WORK IF OB<=15 DEG, BUT NEVER IF OB>20 DEG.");
                if (armor().SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ)
                    BASIC.PRINTLN("IF USING EXTRA-TOUGH ARMOR, NOSE-ONLY SHATTER OCCURS IF SOFT CAP OR HOOD WORKS."); // SOFTSHAT PLATE RULE DEFINED
            }


        } // displayObliquityAndCapsDiscussion





    // TODO: sep calculation from output
        static void displayPenetrationDescription () {

            switch (impact.penetrationType) {
                case Impact.PENETRATION_NONE:
                    penetration_Str = "NO HOLING OF PLATE: Only impact shock & plate distortion damage behind plate\n"+
                        " due to ductile plate &/or a plate backing layer (Projectile may be damaged)";
                    break;
                case Impact.PENETRATION_NONE_SPLINTERS:
                    penetration_Str = "NO HOLING OF PLATE: Impact shock & plate distortion & thrown splinters cause\n"+
                        " damage behind this brittle plate w/o any backing (Projectile may be damaged)";
                    break;
                case Impact.PENETRATION_HOLE_REJECTED_INTACT_BODY:
                    penetration_Str = "PLATE HOLED AND NORMAL PLUG PUNCHED OUT, BUT INTACT PROJECTILE REJECTED\n"+
                        " (No significant projectile damage suffered due to impact)";
                    break;
                case Impact.PENETRATION_HOLE_REJECTED_DAMAGED_BODY:
                    penetration_Str = "PLATE HOLED AND NORMAL PLUG PUNCHED OUT, BUT DAMAGED PROJECTILE REJECTED\n"+
                        " (If nose breaks up at OB<45 deg, part of nose (<= 33% body weight) penetrates)";
                    break;
                case Impact.PENETRATION_SEMI_LOWER_BODY:
                    penetration_Str = "PARTIAL PENETRATION: Broken-up lower body (= 50% body weight) penetrates, but\n"+
                        " projectile nose ricochets (Major damage behind plate if filler explodes/burns)";
                    break;
                case Impact.PENETRATION_SEMI_UPPER_BODY:
                    penetration_Str = "PARTIAL PENETRATION: Broken-up nose & upper body (= 50% body weight) penetrate,\n"+
                        " but projectile lower body rejected (Filler rarely has any effect behind plate)";
                    break;
                case Impact.PENETRATION_COMPLETE:
                    penetration_Str = "COMPLETE PENETRATION ACHIEVED: If Exit Angle > 0, Delta Plug pieces ejected\n" +
                        " (If projectile is broken up, at least 80% of body weight exits plate back)";
                    break;
            }

            BASIC.PRINTLN(penetration_Str);


        } // displayPenetrationDescription





        // PRINT EFFECTS OF IMPACT ON POST-IMPACT PROJECTILE MOTION
        static void displayPostImpactMotionEffects() {

          String WBLG_Str = "wobbling or tumbling ";
          String DFL_Str = "change in direction.";
          String DMG_Str = "damage.";

          if (impact.OBDF > 45) {
            WBL1_Str = WBLG_Str; WBL2_Str = "due to extreme "; WBL3_Str = DFL_Str;
          } else if (impact.OBDF > 30 && impact.NSBRK == 0 && impact.BDYDM == 0) {

            WBL1_Str = "has est. 67% chance of wobbling due to "; WBL2_Str = DFL_Str;
          } else if (impact.OBDF > 15 && impact.NSBRK == 0 && impact.BDYDM == 0) {
            WBL1_Str = "has est. 33% chance of wobbling due to "; WBL2_Str = DFL_Str;
          } else if (impact.OBDF > 30 && (impact.NSBRK > 0 || impact.BDYDM > 0)) {
           WBL1_Str = WBLG_Str; WBL2_Str = "due to "; WBL3_Str = DMG_Str;
          } else if  (impact.OBDF > 15 && (impact.NSBRK > 0 || impact.BDYDM > 0)) {
            WBL1_Str = "has est. 67% chance of ";
            WBL2_Str = WBLG_Str;
            WBL3_Str = "due to ";
            WBL4_Str = DMG_Str;
          } else if (impact.NSBRK > 0 || impact.BDYDM > 0) {
            WBL1_Str = "has est. 33% chance of ";
            WBL2_Str = WBLG_Str;
              WBL3_Str = "due to ";
              WBL4_Str = DMG_Str;
          } else
            WBL1_Str = "is almost always moving nose-first with little or no wobble.";

          BASIC.PRINTLN("Projectile  " + WBL1_Str +  WBL2_Str + WBL3_Str + WBL4_Str);

        } // displayPostImpactMotionEffects




            // DISPLAY SECOND EBL/NBL/HBL INFORMATION, IF USER DESIRES
        static void displaySecondPage(String title) {

            BASIC.CLS(); BASIC.PRINTLN(Util.SPC(12) + title);

            BASIC.PRINTLN(RESNOTE_Str);
            BASIC.PRINTLN(NBL1_Str+ VELLTRU_Str+ " "+ N1_Str);
            BASIC.PRINTLN(NBL2_Str+ VELLSHAT_Str+ " "+ N2_Str); BASIC.PRINTLN(NBL3_Str);
            BASIC.PRINTLN(NBL4_Str+ VELLSHATMAX_Str+ " "+ N3_Str); BASIC.PRINTLN(NBL5_Str);
            BASIC.PRINTLN(NBL6_Str+ VELLND_Str+ " "+ N4_Str); BASIC.PRINTLN(NBL7_Str);
            BASIC.PRINTLN(HBL1_Str+ VELHTRU_Str+ " "+ H1_Str); BASIC.PRINTLN(HBL2_Str); BASIC.PRINTLN(HBL3_Str);
            BASIC.PRINTLN(HBL4_Str+ VELHSHAT_Str+ " "+ H2_Str); BASIC.PRINTLN(HBL5_Str);
            BASIC.PRINTLN(HBL6_Str+ VELHSHATMAX_Str+ " "+ H3_Str); BASIC.PRINTLN(HBL7_Str);
            BASIC.PRINTLN(HBL8_Str+ VELHND_Str+ " "+ H4_Str); BASIC.PRINTLN(HBL9_Str);

            //PRINT EFFECTIVE B.L.
            BASIC.PRINTLN("'Effective' Ballistic Limit =");
            if (!EFFVEL_Str.equals("")) {
            PRINT EFFVEL_Str;
            if ((VHOL <= 4000) && (!PAND_Str.equals(""))) THEN PRINT PAND_Str: PRINT SPC(29);
            END IF
            if (!HBLTONBL_Str.equals(""))
            PRINT HBLTONBL_Str
            else
            PRINT EFFPRINT1_Str; EFFPRINT2_Str;
            if (NVRFLAG == 1)  BASIC.PRINTLN(criticalObliquity);  else BASIC.PRINTLN();
            }

            //PRINT SPECIAL NOTES ON EFFECTIVE B.L.
            if (!NOTE[0].equals(""))
                BASIC.PRINTLN(NOTE[0] + "\n" + NOTE[1]);
            if (!NOTE[2].equals(""))
                BASIC.PRINTLN(NOTE[2]);
            if (!NOTE[3].equals(""))
                BASIC.PRINTLN(NOTE[3]);
            if (!NOTE[4].equals("") && (proj().canBend || (!proj().canBend && (impact.MINEV >= VHOL))))
                BASIC.PRINTLN(NOTE[4]);

        } // displaySecondPage





    final static int ARMOR_EDIT_UB = 0;
    final static int ARMOR_EDIT_Q = 1;
    final static int ARMOR_EDIT_QDAM = 2;
    final static int ARMOR_EDIT_CARTWL = 3;


        //DISPLAY ORIGINAL TABULATED ARMOR PARAMETERS
        //
        static void displayOriginalArmor() {

/* FOO
            jFacehard.BASIC.PRINTLN("      SOFT BACK LAYER THICKNESS 'UB' ALWAYS ROUNDED DOWN TO AN INTEGER VALUE");
            if (armorPlateType == Armor.TYPE_GRUSON || armorPlateType == Armor.TYPE_TC) {
                jFacehard.BASIC.PRINTLN("*** TYPE_GRUSON & TC ARMORS ONLY:  'UB' DECREASES AS PLATE THICKNESS INCREASES ***");
                jFacehard.BASIC.PRINTLN("*** CALCULATED DEFAULT 'UB' FOR CURRENT PLATE IS IN 'ORIGINAL ARMOR' DATA ***");
            }
            jFacehard.BASIC.PRINTLN("FLAGS = '0'(CLEAR), '1'(SET), or, for SOFTSHAT, THKTHN, & CARTWL, '2'(ALTERNATE)");
            jFacehard.BASIC.PRINTLN("ORIGINAL ARMOR PARAMETER VALUES:"; TAB(40); "UB ="; oldArmor.UB; TAB(51); "armor().quality ="; oldArmor.quality; TAB(63); "armor().shellShatteringQuality ="; oldArmor.shellShatteringQuality);
            jFacehard.BASIC.PRINTLN("CARTWL FLAG ="; oldArmor.CARTWL; TAB(20); "armor().isCompound FLAG ="; oldArmor.isCompound; TAB(40);
            jFacehard.BASIC.PRINTLN("THNCHL FLAG ="; oldArmor.isThinlyFaced
            jFacehard.BASIC.PRINTLN("SOFTSHAT FLAG ="; oldArmor.SOFTSHAT; TAB(20); "THKTHN FLAG ="; armor().THKTHNSV
            jFacehard.BASIC.PRINTLN("CURRENT PARAMETER VALUES:"; TAB(40); "UB ="; armor().UB; TAB(51); "armor().quality ="; armor().quality; TAB(63); "armor().shellShatteringQuality ="; armor().shellShatteringQuality
            jFacehard.BASIC.PRINTLN("CARTWL FLAG ="; armor().CARTWL; TAB(20); "CMPND FLAG ="; armor().isCompound; TAB(40);
            jFacehard.BASIC.PRINTLN("THNCHL FLAG ="; armor().isThinlyFaced
            jFacehard.BASIC.PRINTLN("SOFTSHAT FLAG ="; armor().SOFTSHAT; TAB(20); "THKTHN FLAG ="; armor().THKTHN
*/

        }   // displayOriginalArmor **


        //
        // PRINT INFORMATION ON SPLINTERS THROWN FROM PLATE BACK ON HITS BELOW THE HBL
        static void displaySplinterReport() {
/* FOO
            if (NORMPLUGWT <= .05) {
                if (armor().effectiveInchesOfMetalBacking == 0f) {
                    if (CART != Armor.CARTWHEEL_BACKSPALL_NO)
                        FLAKE_Str = "Many dangerous splinters thrown from plate back likely due to impact shock.";
                    else
                        FLAKE_Str = "Few dangerous splinters thrown from plate back due to impact shock.";
                } else
                    FLAKE_Str = "Backing material stops splinters thrown from plate back due to impact shock.";
            }

            if (!FLAKE_Str.equals(""))
                jFacehard.BASIC.PRINTLN(FLAKE_Str);
*/
        } // displaySplinterReport



// FILEBEGIN 2
    // >>>>**** FACE-HARDENED ARMOR PENETRATION PROGRAM BY NATHAN OKUN ****<<<<
    //               >>>>**** VERSION 5.2 OF 7 JUNE 2002 ****<<<<
    //      >>>>**** 2ND PRINT & SUBPROGRAM MODULE "FH52SBM2.BAS" ****<<<<
    //



    // ** END OF MODULE-LEVEL "FH40SBM2.BAS" CODE **

    static void  displayBackingMetalPlateTypes() {
/* FOO
    jFacehard.BASIC.PRINTLN() ; jFacehard.BASIC.PRINTLN("Select general type of backing plate from the following table:"); jFacehard.BASIC.PRINTLN();
    BKNGT1_Str = "Wrought Iron": BKNGQ1_Str = " (armor().quality = 0.6)"
    BKNG1_Str = BKNGT1_Str + "                                                   " + BKNGQ1_Str: BKPRT1_Str = BKNGT1_Str + BKNGQ1_Str
    BKNGT2_Str = "Mild (Medium) Steel thru WWI": BKNGQ2_Str = " (armor().quality = 0.7)"
    BKNG2_Str = BKNGT2_Str + "                                   " + BKNGQ2_Str: BKPRT2_Str = BKNGT2_Str + BKNGQ2_Str
    BKNGT3_Str = "High Tensile Steel thru WWI, Nickel Steel, Post-WWI Mild Steel": BKNGQ3_Str = " (armor().quality = 0.8)"
    BKNG3_Str = BKNGT3_Str + " " + BKNGQ3_Str: BKPRT3_Str = BKNGT3_Str + BKNGQ3_Str
    BKNGT4_Str = "Post-WWI High Tensile Steel & British/Japanese Ducol (D) Steel": BKNGQ4_Str = " (armor().quality = 0.9)"
    BKNG4_Str = BKNGT4_Str + " " + BKNGQ4_Str: BKPRT4_Str = BKNGT4_Str + BKNGQ4_Str
    BKNGT5_Str = "All Special Treatment (homogeneous Krupp-armor grade) Steels": BKNGQ5_Str = " (armor().quality = 1.0)"
    BKNG5_Str = BKNGT5_Str + "   " + BKNGQ5_Str: BKPRT5_Str = BKNGT5_Str + BKNGQ5_Str
    PRINT " 1. " + BKNG1_Str: PRINT " 2. " + BKNG2_Str: PRINT " 3. " + BKNG3_Str
    jFacehard.BASIC.PRINTLN(" 4. " + BKNG4_Str + " 5. " + BKNG5_Str);
    jFacehard.BASIC.PRINTLN();
*/
    }




        // PRINT OLD proj().diameterInInches, proj().totalWeightInPounds, proj().bodyWeightInPounds
        // AND ASK USER FOR NEW VALUES.
    static void inputProjectileDimensionsAndWeight() {


        proj().diameterInInches =
                BASIC.askBoundedFloat("Projectile diameter (D), in inches", .1f, 36f, proj().diameterInInches);

        proj().originalWeight = BASIC.askBoundedFloat("Projectile total weight (WT), in pounds", .1f, 8000f, proj().originalWeight);
        proj().totalWeightInPounds = proj().originalWeight;


        if (proj().capHeadType == Projectile.CAP_TYPE_91_UNCAPPED)
            BASIC.PRINTLN("'CAP HEAD' IS NOT PART OF BODY BECAUSE IT SHATTERS BEFORE NOSE DOES.");



        int overallWeight = (int)proj().originalWeight;

        proj().bodyWeightInPounds = BASIC.askBoundedFloat(
            "Projectile body weight (WB), pounds (" + (overallWeight /2) + " -> " + overallWeight,
            overallWeight /2, overallWeight, proj().bodyWeightInPounds);

        BASIC.PRINTLN();

    } // inputProjectileDimensionsAndWeight


    // display PROJ IMPACT INFORMATION
    //
    static void displayImpactInformation() {

        BASIC.PRINTLN("Projectile Diameter (Caliber)   =" + proj().diameterInInches + "inches -- Nation =" +  proj().nation.name() + "& Type =" + proj().name80);
        BASIC.PRINTLN("Projectile Striking Velocity    =" + impact.strikingVelocityFPS + "ft/sec");
        float OBPRNT = Util.round(impact.obliquity, .01f);
        float EXPRNT = Util.round(impact.exitAngle, .01f);
        PRINT("Angles, degrees:  Obliquity     =" + OBPRNT + "& Exit =");
        if (impact.exitAngle >= 0)
            BASIC.PRINTLN("" + EXPRNT);
        else
            BASIC.PRINTLN(" NOT DEFINED");

        //
        // NOSE COVERING LOSS INFO
        if (proj().bodyWeightInPounds == proj().originalWeight)
            REMV_Str = "Projectile does not use any nose coverings by design.";
        else if (proj().totalWeightInPounds == proj().originalWeight)
            REMV_Str = "All projectile nose coverings intact and in place on impact.";
        else if (proj().totalWeightInPounds == proj().bodyWeightInPounds)
            REMV_Str = "All projectile nose coverings stripped off by prior impact.";
        else if (proj().capHeadType != Projectile.CAP_NONE)
            REMV_Str = "Windscreen and Cap Head stripped off by prior impact.  AP cap intact.";
        else {
            REMV_Str = "Windscreen stripped off by prior impact." + (proj().APCapType < Projectile.APCAP_NONE ?
            "  Hood intact." :  "  AP cap intact.");
        }

        BASIC.PRINTLN(REMV_Str);
        //

        float WTSVPRNT = Util.round(proj().originalWeight, .01f);
        float WTPRNT = Util.round(proj().totalWeightInPounds, .01f);

        // round off to nearest hundredth of a pound
        float WBPRNT = Util.round(proj().bodyWeightInPounds, .01f);

        BASIC.PRINTLN("Projectile Weights, pounds: Original=" + WTSVPRNT + "*Impact=" + WTPRNT + "*Body=" +  WBPRNT);

    }


    // PRINT REMAINING VELOCITY MESSAGES ON SCREEN
    //

    static void  displayRemainingVelocityMessages() {
/* FOO
        if (VDPLUG < 0) {
            RVU_Str = "Projectile Remaining Velocity NOT DEFINED"
            PRINT RVU_Str; //  NO PARTIAL OR COMPLETE PEN
        } else
        //
        // CALC AVE PROJ REMAINING VEL FOR 1-PIECE DAMAGED PEN, IF USED & DIFFERENT FROM 'VDPLUG'
        if (VDPLUG != VR) {
            VTMP = (VDPLUG ^ 2 + VR ^ 2) / 2;
            VTEMP = sqr(VTMP): VTOTAL = INT(VTEMP + .5); // K.E. UNCHANGED
        }
        if (DELTAPLUGWT > .05)
            DPLG_Str = "& Delta Plug "; //  ADD DELTA PLUG, IF THERE
        //
        if ((impact.SHAT == 0) || (impact.penetrationType == PENETRATION_HOLE_REJECTED_DAMAGED_BODY)) THEN
        //UNSHATRD PROJ & UNCAPPED Japanese TYPE 91 AP W/'strikingVelocityFPS'<'VHSHAT' BUT 'strikingVelocityFPS'>'VHOL' ('penetrationType'=3)
            if (obliquity < 45) THEN
            if (impact.penetrationType == PENETRATION_HOLE_REJECTED_INTACT_BODY)
                BSNS3_Str = "No Part of Projectile completely penetrates plate."

            if (impact.penetrationType == PENETRATION_HOLE_REJECTED_DAMAGED_BODY) {
                if ((impact.SHAT = 1) || ((impact.SHAT = 0) && (NSBRK > 0))) {
                    BSNS1_Str = "Nose Pieces ";
                    BSNS3_Str = "Projectile Body up to forward bourrelet fails to completely penetrate.";
                } else
                    BSNS3_Str = "No Part of Projectile completely penetrates plate.";

            }

            if (impact.penetrationType == PENETRATION_SEMI_UPPER_BODY) THEN
                if (NSBRK == 0) THEN
                BSNS1_Str = "Nose & Upper Body ";
                else
                BSNS1_Str = "Nose Pieces & Upper Body";
                END IF
                if (BDYDM < 2) THEN
                BSNS3_Str = "Projectile Lower Body fails to completely penetrate."
                ONEPC_Str = "If Projectile Body not broken up, No Part of Projectile completely penetrates."
                else
                BSNS3_Str = "Projectile Lower Body Pieces fail to completely penetrate."
                END IF
            END IF
            if (impact.penetrationType == PENETRATION_COMPLETE) THEN
                if (BDYDM = 1) THEN
                if (VDPLUG != VR) THEN
                    if (NSBRK = 0) THEN
                    BSNS1_Str = "Nose & Upper Body "
                    else
                    BSNS1_Str = "Nose Pieces & Upper Body "
                    END IF
                    BSNS2_Str = "Lower Body "
                    ONEPC_Str = "If Proj not broken, it "; // ONLY IF PROJ DEFORMED, NOT BROKEN
                else
                    BSNS1_Str = "Entire Projectile "
                END IF
                else if (BDYDM = 2) THEN
                if (VDPLUG != VR) THEN
                    BSNS1_Str = "Nose & Upper Body Pieces "
                    BSNS2_Str = "Lower Body Pieces "
                else
                    BSNS1_Str = "All Projectile Pieces "
                END IF
                else
        //NO PROJ DAMAGE
                BSNS1_Str = "Entire Projectile "
                END IF
            END IF
            else
        //'obliquity'>=45 DEG
            if (impact.penetrationType == PENETRATION_HOLE_REJECTED_INTACT_BODY) THEN
                BSNS3_Str = "No Part of Projectile completely penetrates plate."
            END IF
            if (impact.penetrationType == PENETRATION_HOLE_REJECTED_DAMAGED_BODY) THEN
        // ONLY NOSE DAMAGE OCCURS (ABOVE FORWARD BOURRELET)
                BSNS3_Str = "Entire Projectile ricochets off of plate."
            END IF
            if (impact.penetrationType == PENETRATION_SEMI_LOWER_BODY) THEN
                if (NSBRK = 0) {
                    BSNS1_Str = "Lower Body "
                    BSNS3_Str = "Projectile Nose & Upper Body ricochet off of plate."
                } else {
                    BSNS1_Str = "Lower Body & Some Nose Pieces "
                    BSNS3_Str = "Most of Projectile Nose & Upper Body Pieces ricochet off of plate."
                }
                ONEPC_Str = "If Projectile Body not broken up, All of Projectile ricochets off of plate."
            END IF
            if (impact.penetrationType == PENETRATION_COMPLETE) THEN
                if (BDYDM = 1) THEN
                if (VDPLUG != VR) THEN
                    if (NSBRK = 0) THEN
                    BSNS2_Str = "Nose & Upper Body "
                    else
                    BSNS2_Str = "Nose Pieces & Upper Body "
                    END IF
                    BSNS1_Str = "Lower Body "
                else
                    BSNS1_Str = "Entire Projectile "
                END IF
                ONEPC_Str = "If Proj not broken, it "
                else if (BDYDM = 2) THEN
                if (VDPLUG != VR) THEN
                    BSNS1_Str = "Lower Body Pieces "
                    BSNS2_Str = "Nose & Upper Body Pieces "
                else
                    BSNS1_Str = "All Pieces "
                END IF
                else
        //NO PROJ DAMAGE
                BSNS1_Str = "Entire Projectile "
                END IF
            END IF
            END IF
        else
        //SHATR ('impact.SHAT'=1) EXCEPT 'VHSHAT'<='strikingVelocityFPS'<'VLSHAT' ('penetrationType'=PENETRATION_SEMI_REJECTED_DAMAGED_BODY)
        //  INCLUDING Japanese UNCAPPED TYPE 91 AP W/'strikingVelocityFPS'<'VHSHAT'
            if (obliquity < 45) THEN
            BSNS1_Str = "All Nose Pieces "
            if (VR >= 0) THEN
                if (VDPLUG != VR) THEN
                BSNS2_Str = "Body Pieces "
                else
                BSNS1_Str = "All Pieces "
                END IF
            else
                if (strikingVelocityFPS >= VHSHAT) THEN
                BSNS3_Str = "Projectile Body does not completely penetrate."
                END IF
            END IF
            else
        //'obliquity'>=45 DEG
            BSNS1_Str = "Body & Some Nose Pieces "
            if (VR >= 0) THEN
                if (VDPLUG != VR) THEN
                BSNS2_Str = "Most Nose Pieces "
                else
                BSNS1_Str = "All Pieces "
                END IF
            else
                BSNS3_Str = "Most Projectile Nose Pieces do not completely penetrate."
            END IF
            END IF
        END IF

        if (!BSNS1_Str.equals("")) THEN
            PRINT REMVEL_Str; BSNS1_Str; DPLG_Str;
            if (VDPLUG >= 25) THEN
            PRINT "="; VDPLUG; "ft/sec"
            else
            PRINT "ARE VERY SLOW"
            END IF
        END IF
        if (!BSNS2_Str.equals("")) THEN
            PRINT REMVEL_Str; BSNS2_Str;
            if (VR >= 25) THEN
            PRINT "="; VR; "ft/sec"
            else
            PRINT "ARE VERY SLOW"
            END IF
        END IF
        if (!ONEPC_Str.equals("")) THEN
            if (penetrationFlag == 2) THEN
            PRINT ONEPC_Str; DPLG_Str;
            if (DPLG_Str.equals("")) THEN PRINT "has"; :  else PRINT "have";
            if (VTOTAL >= 25) THEN
                PRINT " Remaining Velocity ="; VTOTAL; "ft/sec"
            else
                PRINT " VERY SLOW Remaining Velocity"
            END IF
            if (!BSNS3_Str.equals("")) THEN PRINT BSNS3_Str
            else
            if (!BSNS3_Str.equals("")) THEN PRINT BSNS3_Str
            PRINT ONEPC_Str
            END IF
        else
            if (!BSNS3_Str.equals("")) THEN PRINT BSNS3_Str
        END IF
        END IF
  */

    } // displayRemainingVelocityMessages


    static void displayVersionPage(int pageNumber) {

        // PRINT DEFINITIONS AND NEW VERSION INFORMATION (5.5 HERE)

        switch (pageNumber) {
            case 1:
                BASIC.PRINTLN("IMPORTANT DEFINITIONS:");
                BASIC.PRINTLN("NBL = MINIMUM STRIKING VELOCITY TO ALLOW PROJECTILE TO COMPLETELY PASS THROUGH"); // 1
                BASIC.PRINTLN("      PLATE WITH THE MINIMUM POSSIBLE REMAINING VELOCITY.  IF PROJECTILE BREAKS"); // 2
                BASIC.PRINTLN("      THEN 80% OF THE BODY MAKES IT THROUGH.  ALWAYS IGNORE AP CAP, HOOD, OR"); // 3
                BASIC.PRINTLN("      WINDSCREEN, IF ANY, IN THIS DETERMINATION. (NAVY BALLISTIC LIMIT)"); // 4
                BASIC.PRINTLN("HBL = MINIMUM STRIKING VELOCITY TO MAKE A HOLE IN THE PLATE OF ROUGHLY CALIBER"); // 5
                BASIC.PRINTLN("      SIZE OR GREATER, PUNCHING OUT A PLUG OF ARMOR THROUGH PLATE BACK, INTACT"); // 6
                BASIC.PRINTLN("      OR BROKEN UP.  MAY BE JUST BELOW OR FAR BELOW NBL, DEPENDING ON PLATE"); // 7
                BASIC.PRINTLN("      TYPE, PROJECTILE TYPE, PROJECTILE CONDITION AFTER IMPACT, & OBLIQUITY."); // 8
                BASIC.PRINTLN("      A LITTLE ABOVE 'THROUGH-CRACK' BALLISTIC LIMIT. (HOLING BALLISTIC LIMIT)"); // 9
                BASIC.PRINTLN("EBL = MINIMUM STRIKING VELOCITY, WHEN IT EXISTS, TO ALLOW PROJECTILE FILLER TO"); // 10
                BASIC.PRINTLN("      EXPLODE PROPERLY (FUZE & BASE PLUG OK AND NO CAVITY CRACKS). NOSE DAMAGE"); // 11
                BASIC.PRINTLN("      WILL ONLY COMPROMISE THIS IF PROJECTILE HAS A VERY LARGE CAVITY (OVER 6%"); // 12
                BASIC.PRINTLN("      FILLER CAN FIT INTO CAVITY, EVEN IF LESS WAS USED) OR USES A NOSE FUZE."); // 13
                BASIC.PRINTLN("      USUALLY, I ASSUME PROJECTILE BODY COMPLETELY INTACT, THOUGH NOSE-ONLY"); // 14
                BASIC.PRINTLN("      SHATTER, AMONG OTHER THINGS, MAY MODIFY THIS. (EFFECTIVE BALLISTIC LIMIT)"); // 15
                BASIC.PRINTLN("OB  = ANGLE OF PROJECTILE CENTERLINE COMPARED TO NORMAL (RIGHT ANGLES) LINE AT"); // 16
                BASIC.PRINTLN("      CENTER OF IMPACT SITE (YAW ASSUMED ZERO OR ALREADY INCLUDED). MAXIMUM OB"); // 17
                BASIC.PRINTLN("      USED (DEG): NO SHATTER-->70; IF SHATTER-->75 W/THICK PLATE, 80 OTHERWISE"); // 18
                BASIC.PRINTLN("EX  = ANGLE PROJECTILE REMAINING VELOCITY VECTOR MAKES WITH NORMAL LINE ABOVE"); // 19
                BASIC.PRINTLN("      WHEN PROJECTILE EXITS PLATE BACK (ALSO COMPUTED FOR PLUG VELOCITY CALCS"); // 20
                BASIC.PRINTLN("      EVEN IF PROJECTILE FAILS TO COMPLETELY PENETRATE, BUT IS ABOVE HBL)."); // 21
                BASIC.PRINTLN("CONTINUE? (Y=Yes/N=Skip Change Information (Any other entry does nothing)) ");
                break;
            case 2:
                BASIC.PRINTLN("VERSION 5.5 CHANGE INFORMATION:");
                BASIC.PRINTLN("(1)  Fixed problem where I had added new projectiles to U.S. Selection Table,");
                BASIC.PRINTLN("       but had not updated my logic to handle this correctly, so Selection 20,");
                BASIC.PRINTLN("       the best U.S. designs, was sometimes being assumed to be Selection 18");
                BASIC.PRINTLN("       by FACEHARD, sometimes giving wrong answers when using either 18 or 20.");
                BASIC.PRINTLN("     THANKS ROY FOR YOUR SHOWING ME THIS PROBLEM!  YOUR REPORT HELPED ME FIND");
                BASIC.PRINTLN("     THE ERROR AND FIX IT FAST!!!");
                BASIC.PRINTLN("(2)  Now give very rough estimated probabilities (33% or 67%) for wobbling in");
                BASIC.PRINTLN("       place of vague 'probably' and 'quite possibly' terms in printouts.");
                BASIC.PRINTLN("(3)  Note that a weak shell that is broken above the Holing Limit, but below");
                BASIC.PRINTLN("       the Navy Limit, for some impact may throw pieces of itself through the");
                BASIC.PRINTLN("       plate, while a stronger shell that does not break will only punch out");
                BASIC.PRINTLN("       armor pieces.  Thus, sometimes a weaker shell is more dangerous!");

                break;
            default:
                BASIC.PRINTLN("VERSION 5.X CHANGES CONTINUED:");
                BASIC.PRINTLN("(X)  "); // 1
                BASIC.PRINTLN("     "); // 2
                BASIC.PRINTLN("     "); // 3
                BASIC.PRINTLN("     "); // 4
                BASIC.PRINTLN("     "); // 5
                BASIC.PRINTLN("     "); // 6
                BASIC.PRINTLN("     "); // 7
                BASIC.PRINTLN("     "); // 8
                BASIC.PRINTLN("     "); // 9
                BASIC.PRINTLN("     "); // 10
                BASIC.PRINTLN("     "); // 11
                BASIC.PRINTLN("     "); // 12
                BASIC.PRINTLN("     "); // 13
                BASIC.PRINTLN("     "); // 14
                BASIC.PRINTLN("     "); // 15
                BASIC.PRINTLN("     "); // 16
                BASIC.PRINTLN("     "); // 17
                BASIC.PRINTLN("     "); // 18
                BASIC.PRINTLN("     "); // 19
                BASIC.PRINTLN("     "); // 20
                BASIC.PRINTLN("     "); // 21
                BASIC.PRINTLN("CONTINUE? (Y=Yes/N=Skip Rest (Any other entry does nothing)) ");
                BASIC.PRINTLN("FINISHED? Y=Yes (Any other entry does nothing) ");
                break;

        }

    } // displayVersionPage


}
