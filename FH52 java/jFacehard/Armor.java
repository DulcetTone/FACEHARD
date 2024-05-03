package jFacehard;

import java.util.Vector;


public class Armor {



    public static final int NUM_BACKING_TYPES = 5;

    public static final int TYPE_UNINITIALIZED = 0;
    public static final int TYPE_GRUSON = 1;
    public static final int TYPE_COMPOUND = 2;
    public static final int TYPE_HARVEYMS = 3;
    public static final int TYPE_HARVEYNIS = 4;
    public static final int TYPE_KCAA = 5;
    public static final int TYPE_KCNA1 = 6;
    public static final int TYPE_KCNA2 = 7;
    public static final int TYPE_AHKC = 8;
    public static final int TYPE_BRWWIKC = 9;
    public static final int TYPE_BR1922KC = 10;
    public static final int TYPE_CA = 11;
    public static final int TYPE_TC = 12;
    public static final int TYPE_VH = 13;
    public static final int TYPE_MNC = 14;
    public static final int TYPE_BTC = 15;
    public static final int TYPE_WWICLA = 16;
    public static final int TYPE_WWIICLA = 17;
    public static final int TYPE_WWIICLA2 = 18;
    public static final int TYPE_AVE1898 = 19;
    public static final int TYPE_AVE1911 = 20;
    public static final int TYPE_AVE1922 = 21;
    public static final int TYPE_AVE1930 = 22;
    public int type;

    String name;

    public static final String[] longNames = {
        "",
        "Gruson Chilled Cast Iron (1868-90) (land fortification dome turrets)",
        "Average Compound (hardened-steel-faced wrought iron) (1880-90)",
        "Harveyized (cemented/carburized/case hardened) Mild Steel (1891-1900)",
        "Harveyized Nickel-Steel (1890-1900) (usual 'Harvey' armor)",
        "German original Krupp Cemented (1894-1918) ('KC a/A' (KC 'Old Type'))",
        "German new KC 'n/A' ('New Type') (1928-36) for 'Pocket BB' turrets only",
        "German improved thick-plate KC n/A (1936-45) (SCHARNHORST & BISMARCK)",
        "Austro-Hungarian Witkowitz KC-type jFacehard.Armor (1898-1918)",
        "British average KC-type jFacehard.Armor manufactured 1911-21 (all manufacturers)",
        "British average KC-type jFacehard.Armor manufactured 1922-30 (all manufacturers)",
        "British average post-1930 Cemented jFacehard.Armor (TYPE_CA)      (all manufacturers)",
        "Italian Terni Cemented jFacehard.Armor (TYPE_TC) (thin face for thick plates) (1935-45)",
        "Japanese Vickers Hardened jFacehard.Armor (VH) (non-cemented) (1937-45)",
        "U.S. Midvale Non-Cemented Class 'A' (1907-12 & one cemented Lot in 1922)",
        "U.S. Bethlehem Thin Chill Class 'A' (1921-25) (also made by Midvale)",
        "U.S. average of all other Class 'A' manufactured 1911-25",
        "U.S. average 1935-1943    Class 'A' (all manufacturers) (original)",
        "U.S. average 1944-1950    Class 'A' (all manufacturers) (improved)",
        "Average of all other KC introduced before 1911 (including U.S. & Britain)",
        "Average of all other KC introduced between 1911 and 1921",
        "Average of all other KC introduced between 1922 and 1930",
        "Average of all other KC introduced after 1930"
    };


    // effective inches of protection are expressed as inches of
    // steel plating.  These constants are used to equate a thickness of
    // wood or cement backing material to an analogously resistive thickness of plating
    private static final float INCHES_WOOD_PER_EFFECTIVE_INCH = 100f;
    private static final float INCHES_CEMENT_PER_EFFECTIVE_INCH = 25f;


    ////CARTWL'  --IF '1', BRITTLE PLATE ALWAYS THROWS LARGE DISK ('CARTWHEEL' OR 'BACK SPALL') FROM BACK; IF '2', HAPPENS ONLY AT HIGH OBLIQUITY
    ////SOFTSHAT'--EXTRA-TOUGH ARMOR THAT, IF '1', ALWAYS SHATRS SOFT-CAPPED PROJ (MOST POST-WWI ARMOR) OR, IF '2', SAME FOR WEAKER SOFT-CAPPED PROJ ('CARDONALD' < CARDONALD_MIDVALE)
    ////THKTHN'  --FLAG FOR BOUNDARY OF THICK- & THIN-PLATE CALCULATIONS (1=LOW 0.35-CAL VALUE/DUCTILE ARMOR; 0=HIGH 0.5-CAL VALUE/BRITTLE ARMOR)



    public final static int CARTWHEEL_BACKSPALL_NO = 0; // not prone to significant backspalling (tone's inference)
    public final static int CARTWHEEL_BACKSPALL_EASILY = 1; // BRITTLE PLATE ALWAYS THROWS LARGE DISK ('CARTWHEEL' OR 'BACK SPALL') FROM BACK
    public final static int CARTWHEEL_BACKSPALL_RESISTANCE = 2; // BACKSPALLING HAPPENS ONLY AT HIGH OBLIQUITY
    public int CARTWL;
    //int CARTWLSV;

    public final static int SHATTER_NO_PROJ = 0; // armor has no special hardness to shatter inbound shells
    public final static int SHATTER_SOFTCAPPED_PROJ = 1; // ALWAYS SHATTERS SOFT-CAPPED PROJ (MOST POST-WWI ARMOR)
    public final static int SHATTER_WEAK_SOFTCAPPED_PROJ = 2; // SAME FOR WEAKER SOFT-CAPPED PROJ ('CARDONALD' < CARDONALD_MIDVALE)
    public int SOFTSHAT;
    //int SOFTSHATSV;

    // was 'TA' in Okun
    // actual (as opposed to apparent) armor thickness in inches at point of impact
    public float inchesOfArmorPlating;



    // was THNCHL in Okun's BASIC
    // if true, denotes VERY THIN FACE LAYER W/REDUCED BREAKAGE ABILITY
    // e.g.: (HARVEY & BETHLEHEM THIN CHILL)
    public boolean isThinlyFaced; //, isThinlyFacedSV;


    // was CMPND in Okun's BASIC
    // when true, armor is TYPE_COMPOUND (STEEL-FACED WROUGHT IRON w/ FACE TOO SOFT TO SHATR MOST STEEL PROJ)
    public boolean isCompound; //, isCompoundSV;

    public boolean isCurved;

    // FLAG FOR BOUNDARY OF THICK- & THIN-PLATE CALCULATIONS
    // 2 == ??
    // 1 ==   LOW 0.35-CAL VALUE/DUCTILE ARMOR
    // 0 == HIGH 0.5-CAL VALUE/BRITTLE ARMOR
    public final static int THKTHN_BRITTLE = 0;
    public final static int THKTHN_DUCTILE = 1;
    public final static int THKTHN_FOOBAR  = 2;
    public int THKTHN; //, THKTHNSV;


    // AVG THICKNESS OF PLATE'S UNHARDENED BACK LAYER, expressed as a
    // percentage of plate thickness
    // (THINNER MEANS MORE SCALING EFFECTS, 65% was USED BY ORIGINAL KRUPP KC)
    // was 'UB' in Okun.. 0-100, Percent Unhardened Back Layer (Scaling Factor step function logic)
    public int UB; //, UBSV;
    public int UBCALC;
    public int UBMAN;


    private final static String metalBackingTypeNames[] = {
        "NO METAL BACKING",
        "Wrought Iron                                                    (Q = 0.6)",
        "Mild (Medium) Steel thru WWI                                    (Q = 0.7)",
        "High Tensile Steel thru WWI, Nickel Steel, Post-WWI Mild Steel  (Q = 0.8)",
        "Post-WWI High Tensile Steel & British/Japanese Ducol (D) Steel  (Q = 0.9)",
        "All Special Treatment (homogeneous Krupp-armor grade) Steels    (Q = 1.0)",
    };


    public static final int BACKING_NONE = 0;
    public static final int BACKING_WROUGHT = 1;
    public static final int BACKING_MEDIUM_STEEL = 2;
    public static final int BACKING_HIGH_TENSILE_STEEL = 3;
    public static final int BACKING_IMPROVED_HIGH_TENSILE_STEEL = 4;
    public static final int BACKING_SPECIAL_TREATMENT = 5;
    private int metalBackingType; // was BTP in Okun

    // was 'MTLBACK' in Okun
    private float inchesOfMetalBacking;

    // was NBK in Okun's code
    private int numberMetalBackingPlates;



    // was 'WDTRU' in Okun
    public float inchesOfWoodBacking;

    // was 'CMTTRU' in Okun
    public float inchesOfCementBacking;


    //  was 'Q' in Okun... Relative jFacehard.Armor Resistance Quality
    // PLATE'S STEEL QUALITY, 0.1->1.0 (relative to TYPICAL WWII ARMOR, larger values == better)
    public float quality; //, qualitySV;

    //  was 'QDAM' in Okun... Relative jFacehard.Armor jFacehard.Projectile Damaging Ability
    //  the PLATE'S RELATIVE PROJ DAMAGE ABILITY (ONLY RARELY DIFFERENT FROM 'quality')
    public float shellShatteringQuality; //, shellShatteringQualitySV;




    public void setMetalBacking(int type, int numPlates, float inches) {
        if (inchesOfMetalBacking <= 0f ||
                type == BACKING_NONE ||
                type > BACKING_SPECIAL_TREATMENT) {
            metalBackingType = BACKING_NONE;
            numberMetalBackingPlates = 0;
            inchesOfMetalBacking = 0f;
        } else {
            metalBackingType = type;
            numberMetalBackingPlates = numPlates;
            inchesOfMetalBacking = inches;
        }
    }


    // 'OBRK' IS MAX OB for non-steel projs TO REMAIN EFFECTIVE IF 'strikingVelocityFPS' < 'VLMT'
    public float OBRK() {
        // COMPOUND ARMOR IS WEAKER AND BREAK ANGLE IS 10 DEG HIGHER
        return isCompound ? 50f : 40f;
    }


   //
    //U.S. WWI-ERA TESTS SHOW BRITTLE FH ARMORS LOST STRENGTH IF <0.55-CAL EFFECTIVE THICKNESS,
    //  REDUCING "MSHAT" FOR SUCH "THIN" ARMOR. LATER, TOUGHER PLATES ("armor.THKTHN" FLAG = 1 OR 2)
    //  ACT AS "THIN" PLATES IF <0.35-CAL ("armor.THKTHN= 1""), BASED ON FRENCH 15CM KC-TYPE TURRET ROOF
    //  PLATE OF *DUNKERQUE* HIT BY BRITISH 15" MK 13A APC PROJ IN 1940. VALUES FOR "THKTHN = 2"
    //  ARE ON BRITISH 15" MK 5A "GREENBOY" PROJ HITS ON 7" WWI BRITISH KC. MODIFICATION BASED ON
    //  BRITISH 15" MK 17B APC PROJ HITS ON 4.9" CA PLATES AT 60 DEG., "THKTHN=1" ARMOR HAS TRUE
    //  THIN FOR <0.25-CAL EFFECTIVE THICKNESS & "THKTHN=2" ARMOR HAS TRU THIN FOR <0.35-CAL
    //  EFFECTIVE THICKNESS.  BOTH NON-ZERO "THKTHN" ARMORS USE A 4-STEP DROP FROM THE THICK VALUE
    //  TO THE TRUE THIN VALUE WITH THE INTERMEDIATE STEPS BISECTING THE RANGE BETWEEN THICK & THIN.
    //  THE 0.3-0.35 THICKNESS PART FOR "THKTHN=1" & THE 0.4-0.45 PART FOR "THKTHN=2", USE 0.625
    //  OF THE DIFFERENCE IN RESULTS, & 0.3 OF THE DIFFERENCE IN REMAINING RANGE (FROM BRIT TESTS).

    public float THIN() {
        switch (THKTHN) {
            case Armor.THKTHN_DUCTILE:
                return .35f;
            case Armor.THKTHN_FOOBAR:
                return .45f;
            case Armor.THKTHN_BRITTLE: // FALLTHROUGH
            default:
                return .55f;
        }
    }

    public float TRUTHIN() {
        return THIN() - .1f;
    }



    public float totalActualInchesOfBacking() {
        return inchesOfWoodBacking + inchesOfCementBacking + inchesOfMetalBacking;
    }

    // was a variable named WD in Okun
    public float effectiveInchesOfWoodBacking() {
        return inchesOfWoodBacking / INCHES_WOOD_PER_EFFECTIVE_INCH;
    }

    // was a variable named CMT in Okun
    public float effectiveInchesOfCementBacking() {
        return inchesOfCementBacking / INCHES_CEMENT_PER_EFFECTIVE_INCH;
    }

    // was a variable named BKEFF in Okun's BASIC
    public float effectiveInchesOfMetalBacking() {
        if (numberMetalBackingPlates * inchesOfMetalBacking <= 0f) return 0f;

        // ALL METAL BACKING PLATES ASSUMED IDENTICAL (USUAL DESIGN)
        // DE MARRE SPACED ARMOR/2
        return
                (float)Math.pow(numberMetalBackingPlates *
                Math.pow(inchesOfMetalBacking * metalBackingQualityRating() / numberMetalBackingPlates, 1.4f),
                        .714286f) /2f;
    }


    // this was tracked in the variable TEFF in Okun's BASIC
    public float totalEffectiveInchesWithBacking() {

        // TOTAL EFFECTIVE THICKNESS OF PLATE + BACKING
        return effectiveInchesOfArmorPlate()  + effectiveInchesOfWoodBacking() +
                effectiveInchesOfCementBacking() + effectiveInchesOfMetalBacking();
    }

    // PEN EFFECTIVE THICKNESS W/O BACKING
    public float effectiveInchesOfArmorPlate() {
        return inchesOfArmorPlating * quality;
    }

    // was TD in Okun's code
    public float effectiveShellShatteringInchesThick() {
        return inchesOfArmorPlating * shellShatteringQuality;
    }

    public float metalBackingQualityRating() {
        return .5f + (metalBackingType / 10f); // GOOD-ENOUGH APPROX HERE
    }

    // PEN EFFECTIVE THICKNESS W/O BACKING
    // was 'TP' in Okun
    public float effectiveInchesThick() {
        return inchesOfArmorPlating * quality;
    }



    public void removeAllBacking() {
        inchesOfWoodBacking = 0;
        inchesOfCementBacking = 0;
        inchesOfMetalBacking = 0;
        numberMetalBackingPlates = 0;


        metalBackingType = BACKING_NONE;
    }



    // was UBCALC in Okun's code
     public int calculatedUB() {

        switch (type) {
            case TYPE_GRUSON:
                if (inchesOfArmorPlating <= 15.75f)
                    return  45; // MINIMUM BACKING LAYER THICKNESS
                if (inchesOfArmorPlating >= 33.07)
                    return  67; // THICKEST PLATE KNOWN (84CM) W/MAX BACKING LAYER THICKNESS


                // LINEAR INCREASE FROM 45% TO 67% UNAFFECTED BACK ASSUMED
                return (int)(45f + 22f * (inchesOfArmorPlating - 15.75f) / 17.32f);


            case TYPE_TC:
                if (inchesOfArmorPlating <= 5.5)
                    return 50; // BB SECONDARY GUN MOUNTS
                else if (inchesOfArmorPlating <= 6.5)
                    return 55; // 'ZARA' CLASS HEAVY CRUISER BELT
                else if (inchesOfArmorPlating <= 7.5)
                    return 60;
                else if (inchesOfArmorPlating <= 10.5)
                    return 65;
                else
                    return 70; // BB BELT & BARBETTE/TURRET/CONNING TOWER

            default:
                return 0;  // this only pertains to the above types
        }
    }



    public boolean hasBeenEdited(Armor original) {
        return (original.UB != UB ||
                original.quality != quality ||
                original.shellShatteringQuality != shellShatteringQuality ||
                original.CARTWL != CARTWL ||
                original.isCompound != isCompound ||
                original.isThinlyFaced != isThinlyFaced ||
                original.SOFTSHAT != SOFTSHAT ||
                original.THKTHN != THKTHN);
    }


    public String getMetalBackingTypeDescription() {
        return Armor.metalBackingTypeName(metalBackingType);
    }

    static public String metalBackingTypeName(int i) {
        if (i < 0 || i > metalBackingTypeNames.length)
            return null;
        return metalBackingTypeNames[i];
    }

    public float getInchesOfMetalBacking() {
        return inchesOfMetalBacking;
    }

    public int getNumberMetalBackingPlates() {
        return numberMetalBackingPlates;
    }

    public int getMetalBackingType() {
        return metalBackingType;
    }



    public static Vector<Armor> list = new Vector<Armor>();


    private static boolean instantiateArmor(int index) {

        Armor a = new Armor();

        boolean initedOk = a.initFromTemplate(index);
        if (initedOk)
            list.addElement(a);
        return initedOk;
    }


    public boolean initFromTemplate(int armorType) {

        // INIT DEFAULT VALUES (SKIPPED IN TABLE IF USED AS-IS)
        quality = 1f;
        UB = 65;
        UBMAN = 0;
        CARTWL = CARTWHEEL_BACKSPALL_NO;
        isCompound = false;
        SOFTSHAT = SHATTER_NO_PROJ;
        THKTHN = 0;
        isThinlyFaced = false;

        switch (armorType) {

            case TYPE_GRUSON:
                shellShatteringQuality = quality = .7f;
                CARTWL = CARTWHEEL_BACKSPALL_EASILY;

                UBMAN = UB = calculatedUB();
                break;

                // 2-TYPE_COMPOUND (67-75% WROUGHT IRON W/CIRCA 450 BRINELL STEEL FACE)
            case TYPE_COMPOUND:
                UB = 70;
                quality = .75f;
                shellShatteringQuality = .6f;
                isCompound = true;
                break;

                // 3-HARVEY MILD STEEL (CIRCA 1 INCH HIGH-CARBON 600-700 BRINELL 'CEMENTED' FACE)
            case TYPE_HARVEYMS:
                UB = 85;
                quality = .78f;
                shellShatteringQuality = .7f;
                isThinlyFaced = true;
                break;

                // 4-HARVEY NI-STEEL (STRONGER STEEL)
            case TYPE_HARVEYNIS:
                UB = 85;
                quality = .805f;
                shellShatteringQuality = .75f;
                isThinlyFaced = true;
                break;

                // 5-GERMAN KC a/A (ORIGINAL KRUPP KC) (TOUGH BACK LAYER, BUT FACE TOO BRITTLE FOR BEST RESULTS)
            case TYPE_KCAA:
                shellShatteringQuality = quality = .828f;
                break;

                // 6-GERMAN KC n/A (IMPROVED 1928 KRUPP KC FOR 'POCKET BATTLESHIP' TURRETS)
                //  (MEDIUM--41%--FACE THICKNESS & HIGHEST FACE HARDNESS EVER USED)
            case TYPE_KCNA1:
                UB = 59;
                shellShatteringQuality = quality = .9f;
                THKTHN = 1;
                break;

                // 7-GERMAN KC n/A (FINAL FURTHER-IMPROVED THICK-PLATE VERSION OF GERMAN KC n/A
                // FOR SCHARNHORST & BISMARCK) (HAS 'SOFTSHAT' CAPABILITY)
            case TYPE_KCNA2:
                UB = 59;
                shellShatteringQuality = quality = .96f;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

                // 8-AUSTRO-HUNGARIAN WITKOWITZ KC (ASSUMES 1911 SKODA 30.5-CM APC = KRUPP 30.5-CM L/3.4 APC)
                // (BEST WWI FH ARMOR--HAS 'SOFTSHAT' CAPABILITY)
            case TYPE_AHKC:
                shellShatteringQuality = quality = .947f;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

                // 9-BRITISH WWI AVERAGE KC
            case TYPE_BRWWIKC:
                shellShatteringQuality = quality = .85f;
                SOFTSHAT = SHATTER_WEAK_SOFTCAPPED_PROJ; THKTHN = 2;
                break;

                // 10-BRITISH AVERAGE KC USED IN HMS NELSON & RODNEY (IMPROVED WWI KC)
            case TYPE_BR1922KC:
                shellShatteringQuality = quality = .9f;
                SOFTSHAT = SHATTER_WEAK_SOFTCAPPED_PROJ; THKTHN = 2;
                break;

                // 11-BRITISH WWII TYPE_CA (THINNEST FACE AFTER 1930 IN ALL THICKNESSES)
            case TYPE_CA:
                UB = 70;
                shellShatteringQuality = quality = .928f;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

                // 12-ITALIAN POST-1930 TERNI CEMENTED (VARIABLE FACE THICKNESS = BEST THICK
                // KC-TYPE & NEAR-BEST THIN KC-TYPE)
            case TYPE_TC:
                shellShatteringQuality = quality = .98f;
                UB = UBMAN = calculatedUB();
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

                // JAP VH (BEST NON-CEMENTED FH ARMOR, BUT BRITTLE--NOT 'SOFTSHAT' TYPE. ONLY
                // POST-1930 FH ARMOR THAT WASN'T 'SOFTSHAT' ARMOR--BASED ON 1910 BRITISH VICKERS KC (VC))
            case TYPE_VH:
                shellShatteringQuality = quality = .839f;
                break;

                // 14-MIDVALE NON-CEMENTED (HEAVIEST--82%--FACE: 50% @ 490-BRINELL & 32% TRANSITION
                // HAS 'SOFTSHAT' CAPABILITY)
            case TYPE_MNC:
                UB = 18;
                shellShatteringQuality = quality = .881f;
                CARTWL = CARTWHEEL_BACKSPALL_EASILY; SOFTSHAT = SHATTER_SOFTCAPPED_PROJ;
                break;

                // 15-BETHLEHEM THIN CHILL (KC-TYPE, BUT THINNEST--ONLY ABOUT 15%--FACE)
            case TYPE_BTC:
                UB = 85;
                quality = .889f;
                shellShatteringQuality = .85f;
                isThinlyFaced = true;
                break;

                // 16-MOST DEFAULTS USED HERE. AVERAGE OF ALL OTHER U.S. CLASS 'A' 1911-1930
                // (MOSTLY CARNEGIE KC ('CKC'; IMPROVED KRUPP KC a/A))
            case TYPE_WWICLA:
                shellShatteringQuality = quality = .889f;
                break;

                // 17-ORIGINAL U.S. WWII CLASS 'A' (THICKEST--55%--FACE FOR WWII FH ARMOR.
                // BEST THIN PLATES, BUT FACE TOO THICK IN THICK PLATES)
            case TYPE_WWIICLA:
                UB = 45;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                shellShatteringQuality = quality;
                break;

                // 18-IMPROVED U.S. WWII CLASS 'A' STARTING IN 1944 (MOST 6-TAPERED-TO-4" TO 8" PLATES)
            case TYPE_WWIICLA2:
                UB = 45;
                shellShatteringQuality = quality = 1.025f;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

                // 19-ALL OTHER KC 1898-1910 (SIMILAR TO KRUPP KC a/A, BUT EVEN MORE BRITTLE--WORST KC-TYPE ARMOR)
            case TYPE_AVE1898:
                shellShatteringQuality = quality = .828f;
                CARTWL = CARTWHEEL_BACKSPALL_RESISTANCE;
                break;

                // 20-ALL OTHER KC 1911-1921 (NOTICABLY IMPROVED KRUPP KC a/A, WITH SOME IMPROVED
                // TOUGHNESS, BUT STILL NO 'SOFTSHAT' CAPABILITY)
            case TYPE_AVE1911:
                shellShatteringQuality = quality = .85f;
                break;

                // 21-ALL OTHER KC 1922-1930 (FURTHER IMPROVED KC a/A; BUT STILL NO 'SOFTSHAT' CAPABILITY, TO MY KNOWLEDGE)
            case TYPE_AVE1922:
                shellShatteringQuality = quality = .9f;
                break;

                // 22-MOST DEFAULTS USED HERE. ALL OTHER POST-1930 KC (VERY IMPROVED KC a/A)
                // (ALL ARE EXTRA-TOUGH 'SOFTSHAT' PLATES--SOFT AP/HOODS CAPS DO NOT WORK VERY WELL)
            case TYPE_AVE1930:
                shellShatteringQuality = quality;
                SOFTSHAT = SHATTER_SOFTCAPPED_PROJ; THKTHN = 1;
                break;

            default:
                return false;
        }

      return true;

    }

    static void classInit() {
        for (int i = 0; instantiateArmor(i+1); i++)
            continue;
    }


}