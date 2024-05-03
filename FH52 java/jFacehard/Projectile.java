package jFacehard;
// This is part of Tony Lovell's Java transcoding and refactoring of Nathan Okun's FACEHARD v55 program
// it was created in 2004-2005, and is not complete by any means.

import jFacehard.Nation;

public class Projectile {


    // NATION == what nation does this shelltype belong to?
    Nation nation;


    public float diameterInInches;


    public float bodyWeightInPounds;  // was WB in Okun


    public float totalWeightInPounds; // was WT in Okun
    public float originalWeight; // was WTSAVE in Okun


    public boolean capHeadHasBeenRemoved;


    public float apCapRemovedPenetrationBonus;  // AP cap removed penetration bonus (may get set when AP cap is removed)


    // this type 91 shells that lose their AP cap during a simulated firing
    // note so here, so they can restore the cap if the shell is fired again (ugly)
    boolean replaceCapWhileRefurbishing;


    // 'APCapType' IS AP CAP TYPE: -1 = HOOD; 0 = NO CAP & NO HOOD; 1 = 'SOFT' AP CAP
    //  (MAX HARDNESS <= 300 BRINELL) (MOST WWI); 2 = 'HARD' AP CAP (INCLUDES JAP
    //  UNCAPPED JAP 15.5/20.3CM TYPE 91 AP PROJ W/'CAP HEAD' FOR HOLING ONLY
    //  (ALL TYPE 88 APC WITH 'CAP HEAD' WERE AP-CAPPED PROJ)); 3 = SMALL/THIN 'HARD' AP
    //  CAP, SUCH AS THOSE ON WWI KRUPP "L/3,2" & "L/3,4" APC && POST-1908 A-H SKODA APC,
    //  WHICH FAILS TO STOP SHATTER IF PLATE DAMAGE-CAUSING EFF THICKNESS IN CAL (TD/D)
    //  IS > 0.67 CAL & OB > 20 DEG (CAPS MERELY COPIES OF OLD SOFT CAP DESIGNS OR THIN,
    //  SUCH AS CAP FOR KRUPP WWII 38CM SPRG.M.K. L/4,6 (CAPPED BASE-FUZED COMMON)).
    public static final int APCAP_HOOD = -1;
    public static final int APCAP_NONE =  0;
    public static final int APCAP_SOFT =  1;
    public static final int APCAP_HARD =  2;
    public static final int APCAP_SMALLHARD =  3;
    public int APCapType;   // I think this is the one the shell is built with
    public int HARD;        // and that this tracks it during simulated impact


    // windscreenAndCapHeadWeight
    // float WCHWT;


    // 'LTCASE' MEANS PROJ BODY WEAK DUE TO LARGE OR EXTRA-LARGE EXPLOSIVE CAVITY,
    //  THE LATTER BASED ON WWI BRITISH 'COMMON, POINTED, CAPPED' (CPC) DESIGN:
    //  2 = PROJ HAS EXTRA-LARGE FILLER CAVITY & BREAKS AT OB = 0 AGAINST A
    //  >0.67-CAL-THICK PLATE (SHATR RESULTS ARE ALSO CHANGED) OR IF ANY NOSE DAMAGE;
    //  1 = PROJ HAS A LARGE CAVITY, WHICH MAKES INTACT PENETRATION MORE DIFFICULT
    //  (SHATR RESULTS ALSO CHANGED), INCLUDING IF NOSE DAMAGED, BUT SMALLER THAN
    //  'LTCASE'=2 PROJ; 0 = NOT APPLICABLE.
    public static final int LTCASE_HEAVYCASE = 0;
    public static final int LTCASE_LARGECAVITY = 1;
    public static final int LTCASE_MEDIUMCAVITY = 2;
    public int LTCASE;


    // 'SHATRES' GIVES PROJ RESISTANCE TO SHATR AT OB = 0 (NORMAL):
    //  0 = RESISTS SHATR (AP CAP); 1 = EASILY SHATRS (WEAK NOSE) (INCLUDES UNCAPPED
    //  JAP TYPE 91 AP WITH OR WITHOUT CAP HEAD STILL IN PLACE ON ARMOR IMPACT);
    //  2 = CHILLED CAST IRON PROJ (VERY WEAK NOSE; TYPE_COMPOUND ARMOR CAN SHATR IF UNCAPPED).
    public static final int SHATRES_HIGH = 0;
    public static final int SHATRES_LOW = 1;
    public static final int SHATRES_VERYLOW = 2;
    public int SHATRES;


    // 'BRAAK' MEANS WEAK PROJ BREAKS IF VEL < NBL AT OB < 50 DEG FOR TYPE_COMPOUND
    //  ARMOR & OB < 40 DEG FOR THE REST: 1 = BREAKS; 0 = DOES NOT. IF SHATR,
    //  IGNORED.
    public boolean BRAAK;


    //  'PLIM' & 'PDAM' ARE DEFAULT PROJ 'QUALITY' FACTORS AT NORMAL IMPACT USED
    //  FOR NAVY & EFFECTIVE B.L., RESPECTIVELY. THEY MAY BE MODIFIED BY DAMAGE
    //  IF OB > 0 &, IF LTCASE=LTCASE_MEDIUMCAVITY, AT OB = 0 AGAINST >0.67-CAL-THICK PLATES.
    public float PLIM;
    public float PDAM;


    // 'criticalAngle' IS MAX DEFLECTION ANGLE ('OBDF') FOR SOME PROJ DESIGNS TO
    //  REMAIN 'EFFECTIVE'. DOES NOT AFFECT PLATE CAUSING DAMAGE. REPLACES
    //  'AED/BED/CED' &, SOMETIMES, 'ALD/BLD/CLD' (0 = NOT USED) (FH ARMOR
    //  THICKNESS SEEMS TO HAVE NO EFFECT ON THESE PROJ, BUT HOMOGENEOUS ARMOR
    //  THICKNESS DOES). NEGATIVE VALUE IS MAXIMUM BRITISH 'canBend' LOGIC OBLIQUITY
    //  WHERE PROJ REMAINS UNBROKEN UNLESS, FOR 'CARDONALD' PROJ ONLY, STRIKING
    //  VEL GOES ABOVE SHATTER NBL 'VLSHAT' (ODD-BALL LOGIC THIS, BUT IT WORKS!)
    public float criticalAngle;

    public float criticalDamageAngle;

    // 'ALD', 'BLD', & 'CLD' MODIFY NBL FOR PLATE THICKNESS & OBLIQUITY DUE TO
    //  PROJ DAMAGE (0 = NOT USED).
    public float ALD, BLD, CLD;

    // 'AED', 'BED', & 'CED' MODIFY 'EFFECTIVE' LIMIT (= PROJ FILLER & FUZE NOT
    //  DAMAGED BY IMPACT='FIT TO BURST') (NOTE THAT OTHER DAMAGE MAY OCCUR)
    //  (-1 = NOT USED).
    public float AED, BED, CED;

    //  'noseDamageAngle' IS MAX DEFLECTION ('OBDF') PROJ NOSE CAN TAKE BEFORE BREAKING.
    //  LIKE 'criticalAngle', THIS DAMAGE ONLY AFFECTS LATER IMPACTS. DAMAGE BLUNTS/
    //  DEFORMS/BREAKS NOSE FROM FORWARD BOURRELET UP. ONLY LTCASE=LTCASE_MEDIUMCAVITY OR COMMON
    //  PROJ WITH APCapType= APCAP_HOOD, PROJ MADE 'INEFFECTIVE'. IF SHATR, IGNORED.
    public float noseDamageAngle;


    //  'canBend' IS SPECIAL SHATTER-ONLY DAMAGE-CAUSE FLAG FOR PROJECTILES THAT
    //  BEND CONSIDERABLY AT OBLIQUE IMPACT, BUT HAVE A DIFFERENT DAMAGE-CAUSING
    //  EFFECT AT NORMAL, CHANGING FROM ONE TO THE OTHER IN THE 0-30 DEGREE RANGE,
    //  THUS 'PLIM' AT NORMAL IF LESS THAN 1! IS LINEARLY INCREASED TO 1! AT 30
    //  DEGREES & ABOVE FOR THOSE PROJECTILES WITH THIS FLAG SET WHEN SHATTER OCCURS.
    public boolean canBend;

    //  SPECIAL "SUPER" PROJECTILE FLAG.  WITH 'canBend' = true, IT MEANS SUPERIOR
    //  BRITISH WWII 15" MARK 17B ROYAL ORDNANCE FACTORY, CARDONALD, SCOTLAND PROJECTILES
    //  CARDONALD = CARDONALD_CARDONALD & WITH 'canBend' = false & 'CARDONALD' = CARDONALD_MIDVALE, IT MEANS SUPERIOR SOFT-CAPPED
    //  WWI U.S. NAVY "MIDVALE UNBREAKABLE" OR "MIDVALE 1916" AP PROJECTILES && ALL LATER
    //  US AP & "SPECIAL" COMMON PROJECTILES WITH SOFT CAPS OR HOODS (IF 'CARDONALD' = 2,
    //  'SOFTSHAT' = 2 ARMOR HAS NO EFFECT AT 'OB'<= 15, AS WITH 'SOFTSHAT' = 0 ARMOR WITH
    //  THE REST OF THE WEAKER SOFT-CAPPED APC PROJECTILES)
    public static final int CARDONALD_NO = 0;
    public static final int CARDONALD_CARDONALD = 1;
    public static final int CARDONALD_MIDVALE = 2;
    public int CARDONALD;  // was CARDONAL in Okun

    public String name;
    public String name80;
    public String printerName;


    public static final int CAP_NONE = 0;
    public static final int CAP_TYPE_91_UNCAPPED = 1;
    public static final int CAP_TYPE_88_91_CAPPED = 2;
    public int capHeadType = CAP_NONE;


    // SPECIAL BEST U.S. PROJ LOGIC FOR IMPACT EXTREME CONDITIONS
    // (NO IMPROVEMENT OVER PREVIOUS DESIGNS IF LOGIC APPLIES)
    public boolean SPECIALEXTREMECONDITIONHANDLING;



    public Projectile(Nation nat) {
        nation = nat;
    }




    // Okun's code repeatedly asks whether the shell has a hood or soft APCAP installed
    // (e.g.: "ABS(HARD) == 1")
    public boolean softCapOrHood() {
        return HARD == APCAP_HOOD || HARD == APCAP_SOFT;
    }


    public void refurbishToFactoryCondition() {
        totalWeightInPounds = originalWeight;
        HARD = APCapType;
        capHeadHasBeenRemoved = false;
        apCapRemovedPenetrationBonus = 0f;

        // hacky... this one particular shell is transformed to another when
        // losing its AP cap.  Refurbishing it requires the reverse make-over
        if (replaceCapWhileRefurbishing) {
            criticalDamageAngle = 15; SHATRES = SHATRES_LOW; noseDamageAngle = 20;
            BRAAK = true; APCapType = Projectile.APCAP_HARD;
            LTCASE = Projectile.LTCASE_LARGECAVITY; PLIM = .945f; PDAM = -1;
            ALD = .00336f; BLD = 1.418f; CLD = .0091701f;
            AED = -1; BED = -1; CED = -1;
            replaceCapWhileRefurbishing = false;
        }
    }


    public void onAPCapRemoved() {
        // LOSS OF AP CAP ASSUMES LOSS OF ALL PROJ NOSE COVERINGS
        if (totalWeightInPounds == bodyWeightInPounds) {
            if (capHeadType == CAP_TYPE_91_UNCAPPED) {
                    // IF UNCAPPED TYPE 91 AP PROJ LOSES "CAP HEAD",
                    // USE PRE-WWI UNCAPPED AP & COMMON PROJ PARAMETERS (DEFAULT #3)

                // this wholesale swap of parameters is remedied
                // by refurbishToFactoryCondition()  (see above)

                replaceCapWhileRefurbishing = true;

                SHATRES = SHATRES_LOW; APCapType = Projectile.APCAP_NONE;
                noseDamageAngle = 5; PLIM = .75f; PDAM = .6f;
                ALD = .000143f; BLD = 2.249f; CLD = .00267f;
                criticalDamageAngle = 0; BRAAK = true; LTCASE = Projectile.LTCASE_LARGECAVITY;
                AED = .000247f; BED = 2.129f; CED = .00172f;
                CARDONALD = CARDONALD_NO;

            } else {
                //  AP CAPS ABSORB ENERGY WHEN DESTROYED, SO REMOVING AN EXISTING CAP INCREASES
                //  PEN ABILITY P-VALUE BY 5%, THOUGH SHATR REDUCES THIS BY 30% AT NORMAL
                //  (HARD & SOFT CAP)
                if (APCapType != Projectile.APCAP_NONE)
                    apCapRemovedPenetrationBonus = .05f; // 5% AP-CAP-REMOVED PENETRATION BONUS
            }
        }
    }

        // MAXIMUM QUALITY FOR SOFT-CAPPED PROJ
    public float SOFTQPMAX() {

        float foo = 1f - (1.1f * ((totalWeightInPounds - bodyWeightInPounds) / totalWeightInPounds) - .0268f);

        if (foo > 1)
            foo = 1; // QUALITY CANNOT BE IMPROVED BY A SMALL SOFT CAP
        return foo;
    }



        // was SUB ALLPROJDATA under Okun
        // we have a database of historical projectile data which can be fetched
        // by nation/number pairs.  You get a copy of the template, so you can alter
        // it as you application requires before conducting a penetration test
    private static boolean instantiateProjectile(Nation nat, int projectileIndex) {

            // the constructor will place the projectile on the projectiles list for the given nation
        Projectile proj = new Projectile(nat);

        boolean initedOk;

            // somewhat ugly... go to the switch-statement encoded database
        if (nat == Nation.US)
            initedOk = proj.initUSProjectile(projectileIndex);
        else if (nat == Nation.UK)
            initedOk = proj.initUKProjectile(projectileIndex);
        else if (nat == Nation.DE)
            initedOk = proj.initDEProjectile(projectileIndex);
        else if (nat == Nation.FR)
            initedOk = proj.initFRProjectile(projectileIndex);
        else if (nat == Nation.IT)
            initedOk = proj.initITProjectile(projectileIndex);
        else if (nat == Nation.JP)
            initedOk = proj.initJPProjectile(projectileIndex);
        else if (nat == Nation.AH)
            initedOk = proj.initAHProjectile(projectileIndex);
        else {
            throw new RuntimeException("jFacehard.Projectile jFacehard.Nation not supported") ;
        }

        if (initedOk)
            nat.projectiles.addElement(proj);
        return initedOk;

    }




    private boolean initUSProjectile(int index) {

        ALD = 0; BLD = 0; CLD = 0; //  indedx = 7-19 use these common values... other indices override them below
        AED = -1; BED = -1; CED = -1;
        CARDONALD = CARDONALD_MIDVALE;
        PDAM = -1; // 'PDAM' IS UNIVERSAL NO-OP HERE (USUALLY SET TO EQUAL 'PLIM' TO NO-OP)

        switch (index) {
            case 1:
                name80 = "Average Army/Navy (A/N) uncapped Chilled Cast Iron (Palliser) AP & Common(*)";
                return initGenericProjectile(1);

            case 2:
                name80 = "Ave. capped Chilled Cast Iron Army Coast Defense (A.C.D.) APC (1900-10)(*)";
                return initGenericProjectile(2);

            case 3:
                name80 = "A/N uncapped Steel 'AP Shot' (0-3.9% black-powder/Ex.D filler)(1890-1910)(*)";
                return initGenericProjectile(5);

            case 4:
                name80 = "A/N soft-capped Steel 'AP Shot'(0-3.9% blk-powder/Ex.D filr)(1898-1910N/45A)";
                return initGenericProjectile(6);

            case 5:
                name80 = "A/N uncapped Steel 'AP Shell'/Common(4-6% blk-powder/Ex.D filler)(1890-1945)";
                return initGenericProjectile(3);

            case 6:
                name80 = "A/N soft-capped Steel 'AP Shell'/Common(4-6% blk-pdr/Ex.D flr)(1898-1945)(*)";
                return initGenericProjectile(4);

            case 7:
                name80 = "Base-fuzed 7/12/14-in 'Bombardment' (Light-case)(9-11% Ex.D filler)(1914-42)";
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                criticalAngle = 0; BRAAK = true; SHATRES = SHATRES_LOW; APCapType = APCAP_NONE;
                LTCASE = LTCASE_MEDIUMCAVITY; noseDamageAngle = 15; PLIM = .748f; PDAM = .6f;
                return true;

            case 8:
                name80 = "Ave. Navy 1911-23 APC except Navy Midvale 8-in Mk 11 (1911) & 'Midvale 1916'";
                criticalAngle = 5; BRAAK = true; SHATRES = SHATRES_HIGH; APCapType = APCAP_SOFT;
                noseDamageAngle = 15; PLIM = .89f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 9:
                name80 = "Navy Midvale 8-in Mk 11 (1911) & all Navy 'Midvale Unbreakable 1916' APC";
                criticalAngle = 5; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_SOFT;
                noseDamageAngle = 15; PLIM = 1; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 10:
                name80 = "Ave. 1921-1935 A.C.D. APC Shot (2-3% Ex. D filler) ('Midvale Army 1921')";
                criticalAngle = 10; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 15; PLIM = .94f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 11:
                name80 = "Ave. post-1935 A.C.D. APC Shot (1.4-2% Ex. D filler) (Ave. Navy AP c.1940)";
                criticalAngle = 20; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 20; PLIM = .94f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 12:
                name80 = "Ave. post-WWI base-fuzed Common (4-5% Ex D filler & windscreen, but no hood)";
                criticalAngle = 10; BRAAK = false; SHATRES = SHATRES_LOW; APCapType = APCAP_NONE;
                noseDamageAngle = 15; PLIM = .85f; LTCASE = LTCASE_LARGECAVITY;
                return true;

            case 13:
                name80 = "Ave. post-WWI hooded base-fuzed Special Common (3-5% Ex. D filler) (1930-45)";
                criticalAngle = 15; BRAAK = false; SHATRES = SHATRES_LOW; APCapType = APCAP_HOOD;
                noseDamageAngle = 15; PLIM = .9f; LTCASE = LTCASE_LARGECAVITY;
                return true;

            case 14:
                name80 = "6-in Mk 27-1-6 hooded base-fuzed Special Common(2.1-2.4% Ex. D filler)(1933)";
                criticalAngle = 20; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HOOD;
                noseDamageAngle = 20; PLIM = .95f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 15:
                name80 = "8-in Mk 15-1 hard-capped base-fuzed Special Common (4.4% Ex. D filler)(1930)";
                criticalAngle = 15; BRAAK = false; SHATRES = SHATRES_LOW; APCapType = APCAP_HARD;
                noseDamageAngle = 15; PLIM = .9f; LTCASE = LTCASE_LARGECAVITY;
                return true;

            case 16:
                name80 = "3-in Mk 29-1/30-1 (to 1944), 8-in Mk 19-1-3 (to 1941) & A.C.D. Mk 20-1 APC";
                criticalAngle = 15; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 15; PLIM = .94f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 17:
                name80 = "8-in Mk 19-4-6 APC";
                criticalAngle = 20; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 15; PLIM = .96f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 18:
                name80 = "6-in Mk 35-1-8 & 16-in Mk 8-1-5 (to 1944) APC";
                criticalAngle = 20; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 20; PLIM = .9f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 19:
                name80 = "8-in Mk 21-1-4, 14-in Mk 16-1-6 (to 1943) & A.C.D. Mk 20-1, and\n" +
                "  16-in Mk 5-1-4 (to 1944) & Mk 5-6 (ex-A.C.D. Mk 12-1 w/Mk 21 BDF) APC";
                criticalAngle = 25; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 25; PLIM = .94f; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 20:
                name80 = "3-in Mk 29-2/30-2, 6-in Mk 35-9-11, 8-in Mk 21-5, 12-in Mk 18-1,\n" +
                    "  14-in Mk 16-7-11, and 16-in Mk 5-5 & Mk 8-6-8 APC";
                criticalAngle = 30; BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                noseDamageAngle = 30; PLIM = 1; LTCASE = LTCASE_HEAVYCASE;
                SPECIALEXTREMECONDITIONHANDLING = true; // this is the only shell for which this applies
                return true;

        }

        return false;
    }


    private boolean commonUKInit(int which) {
        switch (which) {
            case 1:
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                criticalAngle = 0; noseDamageAngle = 15;
                SHATRES = SHATRES_LOW;
                return true;
            case 2:
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                LTCASE = LTCASE_HEAVYCASE; BRAAK = false; noseDamageAngle = 20;
                return true;
            case 3:
                AED = -1; BED = -1; CED = -1;
                BRAAK = false;
                noseDamageAngle = 25;
                return true;
        }
        return false;
    }


    private boolean initUKProjectile(int index) {

        switch (index) {

            case 1: // PALLISER CHILLED CAST IRON
                name80 = "Average Chilled Cast Iron (Palliser) uncapped AP & Common (*)";
                return initGenericProjectile(1);

            case 2: // UNCAPPED "AP SHOT" 1895-21 0-3.9% BLACK-POWDER/HE FILLER
                name80 = "Average uncapped Steel 'AP Shot'/AP (0-3.9% black-powder/HE filler)(1890-23)";
                return initGenericProjectile(5);

            case 3: // UNCAPPED "AP SHELL"/COMMON 1895-18 4-6% BLACK POWDER FILLER
                name80 = "Ave. uncapped Steel 'AP Shell'/Common (4-6% black-powder/HE filler)(1890-23)";
                APCapType = APCAP_NONE; PLIM = .748f; PDAM = .65f;
                LTCASE = LTCASE_LARGECAVITY; BRAAK = true;
                return commonUKInit(1);

            case 4: // CAPPED   "AP SHELL"/COMMON 1905-18 4-6% BLACK-POWDER/HE FILLER
                name80 = "Ave. soft-capped Steel 'AP Shell'/Common (4-6% blk-powdr/HE filler)(1905-23)";
                APCapType = APCAP_SOFT; PLIM = .748f; PDAM = .65f;
                LTCASE = LTCASE_LARGECAVITY; BRAAK = true;
                return commonUKInit(1);

            case 5: // CP
                name80 = "Average uncapped Common, Pointed (CP)(6.6-9.4% black-powder filler)(1900-21)";
                APCapType = APCAP_NONE; PLIM = .728f; PDAM = .5f;
                LTCASE = LTCASE_MEDIUMCAVITY; BRAAK = true;
                return commonUKInit(1);

            case 6: // CPC
                name80 = "Ave. Common, Pointed,(soft)Capped(CPC)(6.6-9.4% black-pdr/Shellite)(1905-25)";
                APCapType = APCAP_SOFT; PLIM = .728f; PDAM = .5f;
                LTCASE = LTCASE_MEDIUMCAVITY; BRAAK = true;
                return commonUKInit(1);

            case 7: // ORIGINAL 6-IN TO 12-IN APC 1905-1912
                name80 = "6 to 12-in first soft-capped cast-steel APC(3.05-3.4% Lyddite filr)(1905-11)";
                return initGenericProjectile(6);

            case 8: // AVE 6-13.5" (LT) APC 1913-1918
                name80 = "6 to 13.5-in(Light) improved cast-steel APC(3.05-3.4% Lyddite filr)(1912-18)";
                APCapType = APCAP_SOFT; PLIM = .985f; PDAM = .985f;
                LTCASE = LTCASE_HEAVYCASE; BRAAK = true;
                return commonUKInit(1);

            case 9: // AVE 13.5" (HY) & 15" APC 1913-1918
                name80 = "13.5(Heavy), 14 & 15-in forged-steel APC(3.15-3.87% Lyddite filler)(1913-18)";
                APCapType = APCAP_SOFT; PLIM = 1; PDAM = 1 ;
                LTCASE = LTCASE_HEAVYCASE; BRAAK = false;
                CARDONALD = CARDONALD_MIDVALE;
                return commonUKInit(1);

            case 10: // 12" MK 7A APC
                name80 = "12-in hard-capped    Mark 7A 'GREEN BOY' APC (2.5% Shellite filler)(1918-45)";
                PLIM = 1; PDAM = 1; criticalAngle = 0;
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                return commonUKInit(2);

            case 11: // 13.5" (HVY), 14", & 15" MK 5A APC
                name80 = "13.5(Heavy)/14/15-in Mark 5A 'GREEN BOY' APC (2.5% Shellite filler)(1918-35)";
                PLIM = 1.02f; PDAM = 1.02f; criticalAngle = 0;
                ALD = .000454554f; BLD = 2.08917437f; CLD = .00514125f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                return commonUKInit(2);

            case 12: // 15" MK 5A BLUE-BAND APC
                name80 = "15-in Mark 5A 'Improved' (special 'blue-band' model) APC           (1921-35)";
                PLIM = 1.02f; PDAM = 1.02f; criticalAngle = 15;
                ALD = 0; BLD = 0; CLD = 0; AED = -1; BED = -1; CED = -1;
                return commonUKInit(2);

            case 13: // POST-WWI CPBC/SAP WITH HOOD
                name80 = "Average post-WWI CPBC/SAP (Hooded & 4-6% TNT- or Shellite filler)  (1921-55)";
                SHATRES = SHATRES_LOW; APCapType = APCAP_HOOD; PLIM = .9f; PDAM = .9f;
                ALD = .000184977f; BLD = 2.46f; CLD = .02549452f;
                criticalAngle = -33;
                LTCASE = LTCASE_LARGECAVITY;
                return commonUKInit(3);

            case 14: // 8" SAPC
                name80 = "Post-WWI 8-in Mark 1B/4B SAPC (4-5% TNT filler)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; PLIM = .93f; PDAM = .93f;
                ALD = .000000122f; BLD = 3.84f; CLD = .001118f;
                criticalAngle = -33;
                LTCASE = LTCASE_LARGECAVITY;
                return commonUKInit(3);

            case 15: // 9.2" GREEN BOY COAST DEFENSE 1919-1935
                name80 = "9.2-in 'GREEN BOY'   APC (3.4% Shellite filler for Coast Defense)  (1919-35)";
                PLIM = 1 ; PDAM = .99f; criticalAngle = 15;
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                return commonUKInit(2);

            case 16: // 9.2" COAST DEFENSE 1935-1950 (STILL 3.4% FILLER)
                name80 = "Ave. 9.2-in Mark 12A APC (3.4% Shellite filler for Coast Defense)  (1935-50)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; PLIM = 1.06f; PDAM = 1.06f;
                ALD = .000232273f; BLD = 2.00692f; CLD = .00096671f;
                criticalAngle = -41;
                LTCASE = LTCASE_HEAVYCASE;
                return commonUKInit(3);

            case 17: // 16" MK 1B APC
                name80 = "16-in Mark 1B (NELSON Class) APC (2048 lb & 2.25% Shellite filler) (1925-45)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; PLIM = 1.01f; PDAM = 1.01f;
                ALD = .000184977f; BLD = 2.46f; CLD = .02549452f;
                criticalAngle = -38;
                LTCASE = LTCASE_HEAVYCASE;
                return commonUKInit(3);

            case 18: // 14-16" POST-1930
                name80 = "14-in Mark 1B/8B (KING GEORGE V Cl.), 15-in Mark 13A/non-Cardonald 17B/22B,\n" +
                "  & 16-in Mark 2B (projected WWII LION Cl.) APC (2.5% TNT filler)  (1935-57)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; PLIM = 1.05f; PDAM = 1.05f;
                ALD = .000184977f; BLD = 2.46f; CLD = .02549452f;
                criticalAngle = -38; LTCASE = LTCASE_HEAVYCASE;
                return commonUKInit(3);

            case 19: // 15" CARDONALD
                name80 = "Royal Ordnance Factory WWII 15-in Mk 17B 'Cardonald' APC (hardened)(1940-45)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; PLIM = 1.05f; PDAM = 1.05f;
                ALD = .000184977f; BLD = 2.46f; CLD = .02549452f;
                criticalAngle = -38; CARDONALD = CARDONALD_CARDONALD; LTCASE = LTCASE_HEAVYCASE;
                return commonUKInit(3);
        }
        return false;
    }


    private boolean commonDEInit(int which) {
        switch (which) {
            case 1:
                ALD = .000143f; BLD = 2.249f; CLD = .00267f;
                AED = .000247f; BED = 2.129f; CED = .00172f;
                criticalAngle = 0; noseDamageAngle = 15;
                BRAAK = true; LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 3:
                ALD = .00006891f; BLD = 2.26f; CLD = .00333f;
                AED = .0000971f; BED = 2.283f; CED = .0035f;
                criticalAngle = 0; BRAAK = true; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                LTCASE = LTCASE_HEAVYCASE;
                return true;

        }
        return false;
    }


    boolean initDEProjectile(int index) {

        // NOTE:  All post-1930 Common & APC projectiles had aluminum windscreens.


        switch (index) {
            case 1:  // TYPE_GRUSON CHILLED CAST IRON AP SHOT & SHELL
                name80 = "Average Chilled Cast Iron (Gruson) AP & Common (*)";
                return initGenericProjectile(1);

            case 2:  // STEEL UNCAPPED AP SHOT UP TO 1918
                name80 = "Average Steel uncapped small-filler 'AP Shot'/AP up to 1918 ('Psgr.') (*)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_NONE;
                PLIM = .794f; PDAM = .754f;
                return commonDEInit(1);

            case 3:  // UNCAPPED COMMON UP TO 1929
                name80 = "Average Steel uncapped, base-fuzed Common ('Spgr.m.Bdz.') up to 1929 (*)";
                SHATRES = SHATRES_LOW; APCapType = APCAP_NONE;
                PLIM = .75f; PDAM = .65f;
                return commonDEInit(1);

            case 4: //  AVE. OF ALL PRE-1911 STEEL APC (PSGR.M.K. L/3,1 & UNDER)
                name80 = "Ave. Steel APC up to 1911 ('Psgr.m.K.' w/'Gr.f.88' or black powder filler)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_SOFT;
                PLIM = .794f; PDAM = .754f;
                return  commonDEInit(1);


            case 5: //  28.3CM ("28CM") PSGR.M.K. L/3,2 & 30.5/38CM PSGR.M.K. L/3,4
                name80 = "Ave. L/3.2 & L/3.4 APC (0.5-cal-KCa/A@30-deg, TNT, & delay-fuzed) (1911-18)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_SMALLHARD;
                PLIM = .794f; PDAM = .754f;
                return commonDEInit(1);

            case 6: //  COMMON WITH HOOD ("GRUNDRING") AFTER 1929
                name80 = "Ave. uncapped, base-fuzed, hooded Common from 1930 on ('Spgr.m.Bdz.u.Hb.')";
                return initGenericProjectile(7);


            case 7: //  38CM & PROJECTED 40.6CM CAPPED COMMON (SPGR.M.BDZ.U.K.)
                name80 = "WWII 38cm & Projected 40.6cm L/4.6 capped Common ('Spgr.m.Bdz.u.K.') (*)";
                return initGenericProjectile(8);


            case 8: //  15CM PSGR.M.K. L/3,7
                name80 = "Post-WWI 15cm   L/3.7 APC (All German Navy 15cm guns from 1925 on)";
                noseDamageAngle = 10; PLIM = .759f; PDAM = .709f; APCapType = APCAP_HARD;
                ALD = .0000243f; BLD = 2.477f; CLD = .00307f;
                AED = .000247f; BED = 2.129f; CED = .00172f;
                criticalAngle = 0; BRAAK = true; SHATRES = SHATRES_HIGH;
                LTCASE = LTCASE_LARGECAVITY;
                return true;

            case 9: //  28,3CM ("28CM") PSGR.M.K. L/3,7
                name80 = "Post-WWI 28.3cm L/3.7 APC ('28cm') ('Pocket BB' main armament)";
                noseDamageAngle = 10; PLIM = .794f; PDAM = .754f; APCapType = APCAP_HARD;
                ALD = .000143f; BLD = 2.249f; CLD = .00267f; //
                AED = .000247f; BED = 2.129f; CED = .00172f;
                criticalAngle = 0; BRAAK = true; SHATRES = SHATRES_HIGH;
                LTCASE = LTCASE_HEAVYCASE;
                return true;

            case 10: // 20,3CM & 30,5CM PSGR.M.K. L/4,4 && 15CM PSGR.M.K. L/4,6
                name80 = "WWII 20.3cm L/4.4 and Prototype 30.5cm L/4.4 & 1944 15cm L/4.6 APC";
                noseDamageAngle = 25; PLIM = .99f; PDAM = .972f;
                return commonDEInit(3);

            case 11: // 28,3CM ("28CM") PSGR.M.K. L/4,4
                name80 = "WWII 28.3cm L/4.4 APC ('28cm')";
                noseDamageAngle = 25; PLIM = .979f; PDAM = .926f;
                return commonDEInit(3);

            case 12: // 38CM PSGR.M.K. L/4,4
                name80 = "WWII 38cm   L/4.4 APC";
                noseDamageAngle = 25; PLIM = .988f; PDAM = .977f;
                return commonDEInit(3);

            case 13: // 40.6CM PSGR.M.K. L/4,4
                name80 = "WWII 40.6cm L/4.4 and Projected 45.7cm & 50.8cm (L/4.4?) APC";
                noseDamageAngle = 25; PLIM = .929f; PDAM = .881f;
                return commonDEInit(3);

            case 14: // LIGHTWEIGHT 38CM & 40.6CM COAST DEFENSE APC (PSGR.M.K.)
                name80 = "Average long-range, light-weight Coast Artillery APC (21/28.3/38/40.6cm)(*)";
                noseDamageAngle = 25; PLIM = .86f; PDAM = .8f;
                return commonDEInit(3);

            case 15: // PROJECTED 53CM "GERAT 36" APC (PSGR.M.K.)
                name80 = "Projected 53cm 'Gerat. 36' (L/4.4?) APC for H-44-Type Battleships (*)";
                noseDamageAngle = 25; PLIM = .9f; PDAM = .86f;
                return commonDEInit(3);
        }

        return false;
    }


    boolean initFRProjectile(int index) {


        switch (index) {
            case 1: // UNCAPPED CHILLED CAST IRON
                name80 = "Average Chilled Cast Iron (Palliser/Gruson) AP & Common (*)";
                return initGenericProjectile(1);

            case 2: // CAPPED   CHILLED CAST IRON
                name80 = "Average Chilled Cast Iron APC (*)";
                return initGenericProjectile(2);

            case 3: // UNCAPPED STEEL AP SHELL
                name80 = "Ave. uncapped Steel AP Shell/Common (4-6% black-powder filler)(1890-1922)(*)";
                return initGenericProjectile(3);

            case 4: // USE UNCAPPED GERMAN SPGR.M.BDZ. (POST-1922)
                name80 = "Ave. uncapped AP Shell/Common (4-6% TNT/British Shellite filler)(1923-60)(*)";
                return initGenericProjectile(7);

            case 5: // SOFT-CAPPED STEEL AP SHELL
                name80 = "Ave. soft-capped AP Shell(4-6% blk-pdr/Melanite/Shellite filler)(1900-45)(*)";
                return initGenericProjectile(4);

            case 6: // 33CM SAPC
                name80 = "33cm APC (1934 SAPC)              (4% Shellite filler) (O.Pf(RC) KMle 34)(*)";
                ALD = .00335f; BLD = 2.13f; CLD = .08701f;
                criticalAngle = 15; LTCASE = LTCASE_LARGECAVITY;
                BRAAK = true; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                AED = -1; BED = -1; CED = -1;
                noseDamageAngle = 20; PLIM = 1 ; PDAM = 1;
                return true;

            case 7: // 38CM FRENCH 1940 APC
                name80 = "38cm APC (original French 1940)   (2% Shellite filler) (O.Pf(RC) KMle 40)(*)";
                ALD = .00336f; BLD = 1.418f; CLD = .0091701f;
                criticalAngle = 20; LTCASE = LTCASE_HEAVYCASE;
                BRAAK = true; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                AED = -1; BED = -1; CED = -1;
                noseDamageAngle = 20; PLIM = 1 ; PDAM = 1;
                return true;

            case 8: // 38CM US CRUCIBLE STEEL CO 380MM AP MK 1
                name80 = "38cm APC (US Crucible Steel AP Mk 1 1943)(2% Ex. D filler)(O.Pf(RC) KMle 43)";
                ALD = 0; BLD = 0; CLD = 0;
                AED = -1; BED = -1; CED = -1; criticalAngle = 30;
                BRAAK = false; SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD; LTCASE = LTCASE_HEAVYCASE;
                noseDamageAngle = 30; PLIM = 1 ; PDAM = 1;
                return true;
        }
        return false;
    }


    boolean initITProjectile(int index) {


        switch (index) {
            case 1: // AVE. BRITISH-TYPE PALLISER/TYPE_GRUSON CHILLED CAST IRON AP & COMMON
                return initUKProjectile(index);

            case 2: // AVE. BRITISH-TYPE UNCAPPED STEEL AP SHOT/AP      C.1900
                return initUKProjectile(index);

            case 3: // AVE. BRITISH-TYPE UNCAPPED STEEL AP SHELL/COMMON C.1900
                return initUKProjectile(index);

            case 4: // AVE. BRITISH-TYPE CAPPED   STEEL AP SHELL/COMMON C.1900
                return initUKProjectile(index);

            case 5: // AVE. BRITISH-TYPE UNCAPPED CP (LIGHT-CASE)
                return initUKProjectile(index);

            case 6: // AVE. BRITISH-TYPE CPC (LIGHT-CASE)
                return initUKProjectile(index);

            case 7: // AVE. BRITISH-TYPE APC ORIGINAL C.1905
                return initUKProjectile(index);

            case 8: // AVE. BRITISH-TYPE APC IMPROVED CAST-STEEL (UP TO 12") C.1912
                return initUKProjectile(index);

            case 9: // AVE. BRITISH-TYPE APC IMPROVED FORGED-STEEL (13.5-15")   FOR PROPOSED BB GUNS
                return initUKProjectile(index);

            case 10: // AVE. BRITISH-12"-MARK 7A-TYPE POST-JUTLAND APC        C.1923
                return initUKProjectile(index);

            case 11: // AVE. BRITISH-13.5"/14"/15"-MARK 5A-TYPE POST-JUTLAND APC FOR PROPOSED BB GUNS
                return initUKProjectile(index);

            case 12: // ITALIAN POST-1930 UNCAPPED COMMON (EST. USING KRUPP 20.3CM SPGR.M.BDZ. W/// GRUNDRING')
                name80 = "Average Italian-design uncapped    Common    (3-6% TNT filler) (1931-50) (*)";
                return initGenericProjectile(7);

            case 13: // ITALIAN POST-1930   CAPPED COMMON (EST. USING KRUPP 38CM   SPGR.M.BDZ.U.K.)
                name80 = "Average Italian-design hard-capped Common    (3-6% TNT filler) (1931-50) (*)";
                return initGenericProjectile(8);

            case 14: // ITALIAN POST-1930 15-38cm APC (EST. BASED ON BRITISH 15" MK 5A IMPROVMENTS)
                name80 = "Average Italian-design 15cm-38cm   APC     (2-2.9% TNT filler) (1931-45) (*)";
                PLIM = 1.02f; PDAM = 1.02f;
                ALD = .0081984f; BLD = 1.119507f; CLD = .005032f;
                AED = -1; BED = -1; CED = -1;
                SHATRES = SHATRES_HIGH;
                APCapType = APCAP_HARD;
                LTCASE = LTCASE_HEAVYCASE;
                criticalAngle = 25; BRAAK = false; noseDamageAngle = 20;
                return true;
        }
        return false;
    }



    private boolean commonJPInit(int which) {
        switch (which) {
            case 1:
                PLIM = .748f; PDAM = .6f; BRAAK = true; LTCASE = LTCASE_LARGECAVITY;
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f; SHATRES = SHATRES_LOW;
                criticalAngle = 0; noseDamageAngle = 15;
                return true;
            case 2:
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f; SHATRES = SHATRES_LOW;
                criticalAngle = 0; BRAAK = true; noseDamageAngle = 15;
                return true;
            case 3:
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                criticalAngle = 15; BRAAK = true; noseDamageAngle = 20; LTCASE = LTCASE_HEAVYCASE;
                return true;
            case 4:
                ALD = .00336f; BLD = 1.418f; CLD = .0091701f;
                BRAAK = true; APCapType = APCAP_HARD; PLIM = .945f;
                return true;
        }
        return false;
    }


    boolean initJPProjectile(int which) {


        /*
        NOTE: Type 88 APC has 2.5% Shimose filler, but Type 91 APC has 2.5% cavity with
        only 1.4-1.6% TNA filler & Type 91 AP has 4% cavity with only 2.5% TNA filler.
        'Cap head' is break-away nose tip lost with windscreen after any impact; it
        functions against face-hardened armor as tip of projectile cap or, for uncapped
        Type 91, as a very limited AP cap only during plate hole formation.  Loss prior
        to plate impact reduces penetration slightly with capped projectiles, but prior
        loss results in a major loss of holing ability in uncapped Type 91 projectiles.
        */

        switch (which) {
            case 1: // PALLISER CHILLED CAST IRON
                name80 = "Average Chilled Cast Iron (Palliser/Gruson) uncapped AP & Common (*)";
                return initGenericProjectile(1);

            case 2: // UNCAPPED AP 1900 W/LARGE BLACK-POWDER FILLER
                name80 = "Ave. uncapped Steel AP Shot(0-3.9% black-powdr/Shimose filler)(1895-1911)(*)";
                return initGenericProjectile(3);

            case 3: // UNCAPPED BASE-FUZED COMMON (4-6% FILLER) (BRITISH COMMON)
                name80 = "Ave. uncapped    Steel AP Shell/Common   (4-6% Shimose filler)  (1915-45)(*)";
                APCapType = APCAP_NONE;
                return commonJPInit(1);

            case 4: // SOFT-CAPPED BASE-FUZED COMMON (4-6% FILLER) (CAPPED DITTO)
                name80 = "Ave. soft-capped Steel AP Shell/Common   (4-6% Shimose filler)  (1915-45)(*)";
                APCapType = APCAP_SOFT;
                return commonJPInit(1);

            case 5: // BRITISH CP (9-10% FILLER)
                name80 = "Ave. uncapped    British CP (6.1-10% black-powder/Shimose filler)(1895-1923)";
                APCapType = APCAP_NONE;
                LTCASE = LTCASE_MEDIUMCAVITY;
                PLIM = .728f; PDAM = .5f;
                return commonJPInit(2);

            case 6: // BRITISH CPC (9-10% FILLER)
                name80 = "14/16.1-in(36/41cm)British CPC(6.1-10% black-powder/Shimose filler)(1912-28)";
                APCapType = APCAP_SOFT; LTCASE = LTCASE_MEDIUMCAVITY;
                PLIM = .728f; PDAM = .5f;
                return commonJPInit(2);

            case 7: // 14-INCH BRITISH PRE-JUTLAND APC 1912-1921
                name80 = "14-in (36cm) British pre-Jutland APC    (3.16% Shimose filler)     (1912-21)";
                APCapType = APCAP_SOFT; LTCASE = LTCASE_HEAVYCASE; PLIM = .985f; PDAM = .985f;
                return commonJPInit(2);

            case 8: // 14-IN BRITISH 'MK 5' APC
                name80 = "36/41cm hard-capped British 'Mark 5'-type APC (2.5% Shimose filler)(1921-28)";
                PLIM = 1.02f; PDAM = 1.01f;
                ALD = .0081984f; BLD = 1.119507f; CLD = .005032f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                return commonJPInit(3);

            case 9: // 20CM, 36CM, & 41CM MK 6/TYPE 88 APC
                name80 = "20cm (1928-45), 36 & 41cm (1928-31) 'Mark 6'/Type 88 APC (Mark 5 w/cap head)";
                PLIM = 1.02f; PDAM = 1.01f;
                ALD = .0081984f; BLD = 1.119507f; CLD = .005032f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                return commonJPInit(3);

            case 10: // ALL CAPPED TYPE 91 AP (APC)
                name80 = "30.5 & 51cm (Proposed) & 36, 41, & 46cm (1931-45) Type 91 APC with cap head";
                AED = .00104f; BED = 1.773f; CED = .00823f; LTCASE = LTCASE_HEAVYCASE;
                criticalAngle = 0; SHATRES = SHATRES_HIGH; noseDamageAngle = 15; PDAM = .85f;
                return commonJPInit(4);

            case 11: // ALL UNCAPPED TYPE 91 AP (SAP)
                name80 = "15.5cm & 20.3cm uncapped Type 91 AP (flat-nosed SAP) with cap head (1931-45)";
                AED = -1; BED = -1; CED = -1; criticalAngle = 15; SHATRES = SHATRES_LOW;
                noseDamageAngle = 20; PDAM = .945f; LTCASE = LTCASE_LARGECAVITY;
                return commonJPInit(4);
        }
        return false;
    }


    boolean initAHProjectile(int which) {

        switch (which) {
            case 1:
                name80 = "Average Chilled Cast Iron (Gruson) AP & Common (*)";
                return initGenericProjectile(1);

            case 2:
                name80 = "Average Chilled Cast Iron APC (*)";
                return initGenericProjectile(2);

            case 3:
                name80 = "Ave. c.1900 uncapped    Steel 'AP Shell'/Common (4-6% blk-pwdr/HE filler)(*)";
                return initGenericProjectile(3);

            case 4:
                name80 = "Ave. c.1900 soft-capped Steel 'AP Shell'/Common (4-6% blk-pwdr/HE filler)(*)";
                return initGenericProjectile(4);

            case 5:
                name80 = "Ave. c.1900 uncapped    Steel 'AP Shot'/AP  (0-3.9% blk-powder/HE filler)(*)";
                return initGenericProjectile(5);

            case 6:
                name80 = "Ave. c.1900 soft-capped Steel 'AP Shot'/APC (0-3.9% blk-powder/HE filler)(*)";
                return initGenericProjectile(6);

            case 7: // SKODA BRITISH-TYPE CP (9-10% FILLER) (UNCAPPED)
                name80="British-type CP (no AP cap)      (9-10% black-powder/HE filler) (1905-18)(*)";
                APCapType = APCAP_NONE; LTCASE = LTCASE_MEDIUMCAVITY; PLIM = .66f; PDAM = .5f;
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f; SHATRES = SHATRES_LOW;
                criticalAngle = 0; BRAAK = true; noseDamageAngle = 15;
                return true;

            case 8:
                name80 = "Hard-capped 'AP Shell'/Common  (E.Gr.)(4-6% blk-pwdr/HE filler) (1909-18)(*)";
                return initGenericProjectile(8);

            case 9: // SKODA BRITISH-TYPE (BUT HARD-CAPPED) CPC (9-10% FILLER)(Z.GR.)
                name80 = "British-type CPC(w/hard AP cap)(Z.gr.)(9-10% blk-pwdr/HE filler)(1909-18)(*)";
                APCapType = APCAP_HARD; LTCASE = LTCASE_MEDIUMCAVITY; PLIM = .66f; PDAM = .5f;
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f; SHATRES = SHATRES_LOW;
                criticalAngle = 0; BRAAK = true; noseDamageAngle = 15;
                return true;

            case 10: // SKODA WWI APC (P.GR.)
                name80 = "Improved Skoda APC(P.Gr.)(25%-Ni hard AP cap & 2-3.5% blok-TNT flr)(1909-18)";
                SHATRES = SHATRES_HIGH; APCapType = APCAP_HARD;
                PLIM = .83f; PDAM = .78f;
                ALD = .000143f; BLD = 2.24f; CLD = .00267f;
                AED = .000247f; BED = 2.129f; CED = .00172f;
                criticalAngle = 0; noseDamageAngle = 15; BRAAK = true; LTCASE = LTCASE_HEAVYCASE;
                return true;
        }
        return false;
    }


    // these basic flavors are used from initGenericProjectile
    private boolean commonGenericInit(int which) {
        switch (which) {
            case 1: //  for default templates 5 & 6 (COMMON DATA f1)
                ALD = .0004301f; BLD = 1.845f; CLD = .0027f;
                AED = .03322f; BED = 1.172f; CED = .02222f;
                criticalAngle = 0;
                BRAAK = true;
                SHATRES = SHATRES_LOW;
                noseDamageAngle = 15;
                return true;
            case 2: //  for default templates 2, 3, 4, 7, & 8    (COMMON DATA f2)
                AED = .000247f; BED = 2.129f; CED = .00172f;
                ALD = .000143f; BLD = 2.249f; CLD = .00267f;
                criticalAngle = 0;
                BRAAK = true;
                return true;
            case 3: //  for default templates 1, 2, 3, 4, 7, & 8 (COMMON DATA f3)
                ALD = .000143f; BLD = 2.249f; CLD = .00267f;
                criticalAngle = 0;
                BRAAK = true;
                return true;
        }

        return false;
    }


    private boolean initGenericProjectile(int which) {

        switch (which) {
            //
            //** DEFAULTS #1-9:  ESTIMATES FOR PROJ WITH LITTLE DATA
            case 1: //  AVE PRE-1900 UNCAPPED CHILLED CAST IRON AP (PALLISER/TYPE_GRUSON)
                PLIM = .6f; PDAM = .5f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_VERYLOW; APCapType = APCAP_NONE; noseDamageAngle = 5;
                AED = -1; BED = -1; CED = -1;
                return commonGenericInit(3);

            case 2: //  AVE CHILLED CAST IRON APC (U.S. ARMY COAST DEFENSE C.1900, ETC.)
                PLIM = .6f; PDAM = .5f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_VERYLOW; APCapType = APCAP_SOFT; noseDamageAngle = 5;
                return commonGenericInit(2);

            case 3: //  AVE C.1900 UNCAPPED    STEEL "AP SHELL"/COMMON (4-6% BLACK-POWDER/HE FILLER)
                PLIM = .75f; PDAM = .65f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_LOW; APCapType = APCAP_NONE; noseDamageAngle = 15;
                return commonGenericInit(2);

            case 4: //  AVE C.1900 SOFT-CAPPED STEEL "AP SHELL"/COMMON (4-6% BLACK-POWDER/HE FILLER)
                PLIM = .75f; PDAM = .65f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_LOW; APCapType = APCAP_SOFT; noseDamageAngle = 15;
                return commonGenericInit(2);

            case 5: //  AVE C. 1900 UNCAPPED    STEEL  "AP SHOT"/AP (0-3.9% BLACK POWDER/HE FILLER)
                PLIM = .83f; PDAM = .75f; LTCASE = LTCASE_HEAVYCASE; APCapType = APCAP_NONE;
                return commonGenericInit(1);

            case 6: //  AVE C. 1900 SOFT-CAPPED STEEL "AP SHOT"/APC (0-3.9% BLACK POWDER/HE FILLER)
                PLIM = .83f; PDAM = .75f; LTCASE = LTCASE_HEAVYCASE; APCapType = APCAP_SOFT;
                return commonGenericInit(1);

            case 7: //  GERMAN WWII HOODED BASE-FUZED COMMON ('SPGR.M.BDZ.U.HB.' WITH 'GRUNDRING')
                PLIM = .86f; PDAM = .768f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_LOW; APCapType = APCAP_HOOD; noseDamageAngle = 15;
                return commonGenericInit(2);

            case 8: //  GERMAN WWII THIN-CAPPED BASE-FUZED COMMON ('38CM & 40.6CM SPGR.M.BDZ.U.K. L/4,6')
                PLIM = .86f; PDAM = .768f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_LOW; APCapType = APCAP_SMALLHARD; noseDamageAngle = 15;
                return commonGenericInit(2);

            case 9: //  SKODA C.1910 HARD-CAPPED STEEL "AP SHELL"/COMMON (4-6% BLACK-POWDER/HE FILLER)
                PLIM = .75f; PDAM = .65f; LTCASE = LTCASE_LARGECAVITY;
                SHATRES = SHATRES_LOW; APCapType = APCAP_SMALLHARD; noseDamageAngle = 15;
                return commonGenericInit(2);
        }
        return false;
    }



    // this should be called ONCE only
    static void classInit() {

        // loop through all list and instantiate for each as many projectile types as this
        // rather ugly database logic above seems to support
        for (int i = 0; i < Nation.list.size(); i++) {
            Nation n = (Nation)Nation.list.elementAt(i);
            for (int j = 0; instantiateProjectile(n, j + 1); j++)
                continue;
        }

    }


}
