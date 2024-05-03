package jFacehard;
// This class simulates the impact of a given Shell against a given jFacehard.Armor
// it is derived from Nathan Okun's FACEHARD program, version 52 to 55 or so


public class Impact {
    public Projectile proj;
    public Armor armor;


    public Impact() {
    }


    // values for penetrationType

    public final static int PENETRATION_NONE = 0;
    public final static int PENETRATION_NONE_SPLINTERS = 1;
    public final static int PENETRATION_HOLE_REJECTED_INTACT_BODY = 2;
    public final static int PENETRATION_HOLE_REJECTED_DAMAGED_BODY = 3;
    public final static int PENETRATION_SEMI_LOWER_BODY = 4;
    public final static int PENETRATION_SEMI_UPPER_BODY = 5;
    public final static int PENETRATION_COMPLETE = 6;

    // This is am output of the simulated impact
    public int penetrationType;


    public float criticalObliquity;

    public float PENCONST; // CONSTANT PEN FORMULA ('CORE' TERM)


    public float LCMOD;
    public float POLMOD;
    public float POIMOD;
    public float MSHAT;
    public float PNLSHAT;
    public float CRTAPR;
    public float MAXDIFF;
    public float OB45;
    public float VLMT;
    public float NORMPLUGWT;

    // CHECK IF THESE CAN BE REDUCED IN SCOPE

    public float OBDF;
    public float VHSHAT;
    public float VHSHATMAX;
    public float VLSHAT;
    public float VLSHATMAX;
    public float VHDAM;
    public float VRATMIN;
    public float XDAM;
    public float VRAT;
    public float SNCSMAX;
    public float SHATVDFPR;
    public float VDFCALC;
    public float VRSHATNS;
    public float VNPLUG;
    public float KETOTAL;
    public float KEPUNCH;
    public float BFRACT;
    public float TOPVEL;
    public float VSCRIT;
    public float DIF1;
    public float VDFBND;
    public float VHND;
    public float PNI;
    public float VITRU;
    public float VSCHECK;
    public float TMPOBDF;

    public float V1;
    public float KE1;
    public float V2;
    public float KE2;
    public float V2NBL;

    public float TOTPLUGWT;
    public float DELTAPLUGWT;
    public float NPWTPR;
    public float SHATVDF;
    public float VHTRU;
    public float VLND;
    public float VLTRU;
    public float VDFUSEDPR;
    public float VDFSTD;
    public float VDFUSED;
    public float VR;
    public float VDPLUG;
    public float PSHMAX;
    public float VSPR;
    public float KEVSPR;
    public float KEOBMNSEX;
    public float NDAP;
    public float VRPR;
    public float VLDAM;
    public float EXNBL;
    public float VRATVEL;

    // END OF CHECK

    // TODO:  move these?
    String H1_Str, H2_Str, H3_Str, H4_Str;
    String N1_Str, N2_Str, N3_Str, N4_Str;


    public int BRK;
    public int NSBRK;
    public int BDYDM;


    public float WCHWT; // windscreen + caphead weight
    public float WWT;   // windscreen weight


    public boolean BRAIK;
    public boolean curveFlag = false; // CRVFLAG
    public boolean useCurveRule;      // CRVRL

    public boolean CRTGD; // criticalDamageAngle/noseDamageAngle COMBINED TEST NOT DONE & LOWER BODY ASSUMED OK;

    public boolean SHAT;
    public int NSSHAT;
    public float CAPHDLOSS;


    public int strikingVelocityFPS = 1500; // VS ... feet per second upon striking plate

    public final static int PENFLAG_NO_LARGE_HOLE = 0;
    public final static int PENFLAG_HOLING = 1;
    public final static int PENFLAG_COMPLETE = 2;
    public int penetrationFlag;          // PENFLG


    public float obliquity;      // OB

    public float exitAngle;      // EX
    public float exitAngleMIN;   // EXMIN

    public float MINEV, MINEV1, MINEV2, MINEV3, MINEV4, MINEV5;
    public float VHOL;

    public int CART;
    public int HF;  // // 'HF' = 1 MEANS NO UNSHTRD COMPLETE PEN (obliquity > 70 DEG)


    // UNSHATRD PROJ 'MO' INTERPOLATION TABLE , indexed 0 -> 15 inclusive
    // NOTE:  M[14] IS FOR 70 DEG [MAX obliquity ALLOWED IF NO SHATR]
    static final float[] M = { 1f, 1.045f, 1.09f, 1.135f, 1.18f,
           1.235f, 1.31f, 1.4f, 1.53f, 1.695f, 1.9f,
            2.3f, 3.2f, 4.9f, 8f, 15f };

    // that mysterious variable interpolated from the above
    float MO;

        //
        // NOTES:  MS[16] IS FOR 80 DEG (MAX obliquity ALLOWED FOR THIN PLATE SHATR), indexed 0-17 inclusive

        //SHATRD PROJ 'MSHAT' INTERPOLATION TABLE FOR 'THIN' PLATES [RELATIVE TO NORMAL obliquity VALUE]
        //THICK PLATES USE FORMULA UP TO 75 DEG IN SUB "setObliquityMultipliers" IF SHATR OCCURS OR FOR HIGH-obliquity HBL
    static final float[] MS = {
            1f, 1.002f, 1.0078f, 1.0176f, 1.0314f,
            1.0495f, 1.0722f, 1.0994f, 1.1317f, 1.1672f,
            1.2018f, 1.2377f, 1.2782f, 1.3236f,
            1.3715f, 1.429f, 1.51f, 1.6036f };


            // INVERSE OF VEL TO THICKNESS EXPONENT (= 0.8264463)
    final static float VXP = 1 / 1.21f;

    final static float VDFSTDWW1 = .1256839f; // STANDARD WWI VEL DIFF (100*%(NBL-HBL)) BETWEEN HBL & NBL (W/O SPECIAL LOGIC)
    final static float VDFSTDWW2 = .09f; // STANDARD WWII VEL DIFF (100*%(NBL-HBL)) BETWEEN HBL & NBL (W/O SPECIAL LOGIC)
    final static float VDFBRK = .02f;  // VEL DIFF IF PROJ ALWAYS BREAKS IF IT DOES NOT PENETRATE COMPLETELY (100*%(NBL-HBL)) BETWEEN HBL & NBL (W/O SPECIAL LOGIC)




    //
    //* SELECT APPLICABLE LIMIT VEL & CALC EXIT ANGLE 'exitAngle' *
    // MAX 'exitAngle'= obliquity = NO DEFLECTION.
    // exitAngle' = 0 AT HBL. W/SHATR, NBL IS NOT INVOLVED IN 'exitAngle' CALC ('exitAngleMIN' = 0).
    // W/O SHATR:
    //   BETWEEN HBL & NBL, 'exitAngle' GOES FROM 0 TO OB LINEARLY AS VEL INCREASES.
    //   IF obliquity <= 15 DEG, AT NBL 'exitAngle' = OB (NO DEFLECTION)
    //   IF obliquity > 15 DEG, AT NBL 'exitAngle' = 15 DEG = 'exitAngleMIN' FOR VEL >= NBL

    void BLPLUSEX() {

        //INIT VARIABLES
        N1_Str = N2_Str = N3_Str = N4_Str = "";
        H1_Str = H2_Str = H3_Str = H4_Str = "";

        penetrationFlag = PENFLAG_COMPLETE;
        VRAT = 0; OB45 = 0;
        VHDAM = 0; VLDAM = 0; // INIT DAMAGE LIMITS 'VHDAM' & 'VLDAM' USED IN 'exitAngle' LOGIC

        // CURVED-PLATE RULE CAN APPLY when armor curved, high obliq, and low shatter resist
        curveFlag = (armor.isCurved && obliquity > 45 && proj.SHATRES != Projectile.SHATRES_VERYLOW) ;

        exitAngleMIN =  (obliquity <= 15 ? obliquity : 15); // UNSHATRD PROJ
        CRTGD = false; // DEFAULT 'criticalDamageAngle'/'noseDamageAngle' COMBINED TEST FLAG IS SET TO FAILURE
        VSCHECK = 0; // FOR DEFORMING BRITISH PROJECTILES

        if (SHAT && proj.HARD != 2 && strikingVelocityFPS >= VHSHAT)
            exitAngleMIN = 0;

        // SHATR HAS NO 'exitAngleMIN' ABOVE HBL (EXCEPT IF UNCAPPED Japanese TYPE 91 AP 'strikingVelocityFPS' < SHATRD HBL)
        float MAXDF = obliquity - exitAngleMIN;
        if (MAXDF > 45)
            MAXDF = 45;

        //FORMULA DOES NOT WORK FOR (obliquity - exitAngleMIN) > 45 DEG WITH F.H. ARMOR, SO EXTRA LOGIC RQD
        SNCSMAX = (float)(Util.sin(MAXDF) * Util.cos(MAXDF)); // MAX CHANGE IN DIRECTION FUNCTION

        // *
        // * SELECT ALL LIMIT VEL THAT APPLY TO THIS IMPACT
        // ( '-'  MEANS FOR PENETRATION & '#'  MEANS FOR POST-IMPACT EFFECTS)*
        if (!SHAT || proj.HARD == Projectile.APCAP_HARD) {
            // NO SHATR OR SHATR W/HARD AP CAP (ONLY USED FOR JAP UNCAPPED TYPE 91 PROJ (// HARD// =1 & // SHAT// =1)).
            //    JAP CAP-HEAD DOES NOT STOP SHATR, BUT DOES ALLOW PROJ TO USE UNSHATRD HBL LOGIC.
            // if NO PEN-LIMITING DAMAGE, USE BEST-POSSIBLE NBL.
            if (SHAT) {
                // JAPANESE UNCAPPED TYPE 91 AP ONLY
                VLMT = VLSHAT; VLDAM = VLSHAT; N2_Str = "-#-"; // SHATR OF HARD-CAPPED PROJ
                VHOL = VHTRU; VHDAM = VHTRU; H1_Str = "-#-"; // HOLING BL USES UNSHATRD VALUES
                if (VHOL >= VHSHAT) {
                    // REGULAR HOLING BL LOGIC FOR HIGH obliquity APPLIES
                    VHOL = VHSHAT; VHDAM = VHSHAT; H1_Str = ""; H2_Str = "-#-";
                }
                if (VHSHAT == VHSHATMAX) {
                    N2_Str = ""; N3_Str = "-#-"; // BEST POSSIBLE SHATR RESULT
                    if (H2_Str.equals("-#-")) { H3_Str = "-#-"; }
                }
            } else {
                // if VLTRU>VLND  (PLIM<MAX POSSIBLE) & VLDAM==VLND, A STEP IN MOST VALUES OCCURS AT VLMT
                VLMT = VLTRU; VLDAM = VLTRU; N1_Str = "-#-"; // DEFAULT VALUES (INCLUDING CRITAGL FAILURE)
                VHOL = VHTRU; VHDAM = VHTRU; H1_Str = "-#-";
                if (proj.canBend && proj.CARDONALD == Projectile.CARDONALD_NO && obliquity >= criticalObliquity) {
                    VSCHECK = 9999; // BRITISH NON-CARDONALD DEFORMABLE PROJ ALWAYS BREAKS (criticalObliquity=-1 if NOT USED)
                } else if (proj.CARDONALD == Projectile.CARDONALD_CARDONALD && obliquity >= criticalObliquity) {
                    VSCHECK = VSCRIT; // BRITISH CARDONALD PROJ BREAKS BELOW CRITICAL VEL ONLY
                } else if (proj.canBend && obliquity < criticalObliquity) {
                    VSCHECK = 0; // NO-OP FOR BRITISH DEFORMABLE PROJ AT BELOW CRITICAL OBLIQUITY
                } else {
                    VSCHECK = -1; // NO-OP FOR ALL OTHER PROJ
                }
                if (VLTRU == VLND) {
                    VLMT = VLND; VLDAM = VLND; N1_Str = ""; N4_Str = "-#-"; // DAMAGE DOES NOT OCCUR || DOES NOT CHANGE PENETRATION ABILITY
                    VHOL = VHND; VHDAM = VHND; H1_Str = ""; H4_Str = "-#-";
                } else if ((VSCHECK == -1 && VLTRU > VLND && VITRU >= 0 && strikingVelocityFPS >= VITRU) ||
                        VSCHECK == 0 || (VSCHECK > 0 && strikingVelocityFPS >= VSCHECK)) {
                    VLDAM = VLND; N1_Str = "--"; N4_Str = "-#-"; // NO DAMAGE OCCURRED, BUT WOULD BELOW EFFECTIVE BL
                    VHDAM = VHND; H1_Str = "--"; H4_Str = "-#-";
                }
                // if obliquity <= 45 DEG, MAX (WORST-CASE DAMAGE) NOSE-FIRST HBL & NBL ARE SHATRD PROJ LIMITS.
                //   THIS CAN OCCUR WITH AN UNDAMAGED (VLMT==VLND) PROJ if A "NON-THICK" PLATE IS HIT.
                // if obliquity > 45 DEG, ONLY HBL RESTRICTED
                if ((obliquity <= 45) && !proj.canBend) {
                    // AT LOW obliquity WHERE NOSE-FIRST PEN OCCUR, VLSHATMAX IS WORST POSSIBLE CASE FOR UNDAMAGED NBL
                    //   if VS<EFFECTV BL, VLSHAT IS WORST CASE, if NO NOSE-FIRST PEN OCCURS.
                    //   FOR WWII BRITISH DEFORMABLE PROJ, THIS DOES NOT SEEM TO BE TRUE.
                    if ((N4_Str.equals("-#-") || (N4_Str.equals("-#-") && VLSHAT == VLSHATMAX)) && VLMT >= VLSHAT) {
                        VLMT = VLSHATMAX; VLDAM = VLSHATMAX; // NBL LID FOR UNDER 45 DEG
                        N1_Str = ""; N2_Str = ""; N4_Str = ""; N3_Str = "-#-";
                        VHOL = VHSHATMAX; VHDAM = VHSHATMAX;
                        H1_Str = ""; H2_Str = ""; H4_Str = ""; H3_Str = "-#-";
                    } else if (N4_Str.equals("-#-") && VLMT >= VLSHAT) {
                        VLMT = VLSHAT; VLDAM = VLSHATMAX; // DITTO
                        N1_Str = ""; N4_Str = ""; N2_Str = "--"; N3_Str = "-#-";
                        VHOL = VHSHAT; VHDAM = VHSHATMAX;
                        H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                    } else if (VLMT >= VLSHAT) {
                        VLMT = VLSHAT; VLDAM = VLSHAT; // DITTO FOR // VLTRU//  <> // VLND//
                        VHOL = VHSHAT; VHDAM = VHSHAT;
                        if (N1_Str.equals("-#-")) {
                            N1_Str = ""; N3_Str = ""; N4_Str = ""; N2_Str = "-#-"; // LID if IMPACT & POST-IMPACT DO NOT USE // VLND//
                            H1_Str = ""; H3_Str = ""; H4_Str = ""; H2_Str = "-#-";
                            if (VLSHAT == VLSHATMAX) {
                                N2_Str = ""; N3_Str = "-#-"; // if // VLSHAT// =// VLSHATMAX//
                                H2_Str = ""; H3_Str = "-#-";
                            }
                        } else {
                            N1_Str = ""; N4_Str = ""; N2_Str = "--"; N3_Str = "-#-"; // LID if POST-IMPACT USES // VLND//
                            H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                            if (VLSHAT == VLSHATMAX) {
                                N2_Str = ""; N3_Str = "-#-"; // if // VLSHAT// =// VLSHATMAX//
                                H2_Str = ""; H3_Str = "-#-";
                            }
                        }
                    } else if (VHOL >= VHSHAT) {
                        // HBL LID FOR NON-BRITISH PROJ AT UNDER 45 DEG
                        if (VHDAM < VHOL) {
                            if (VHDAM > VHSHATMAX) {
                                VHDAM = VHSHATMAX; XDAM = 1;
                            } else {
                                XDAM = 2;
                            }
                        } else {
                            XDAM = 0;
                        }
                        if (N4_Str.equals("-#-") || (N4_Str.equals("-#-") && VLSHAT == VLSHATMAX)) {
                            VHOL = VHSHATMAX;
                            if (XDAM == 2) {
                                if (N4_Str.equals("-#-")) {
                                    H1_Str = ""; H2_Str = ""; H3_Str = "--"; H4_Str = "-#-";
                                }
                            } else {
                                VHDAM = VHSHATMAX;
                                H1_Str = ""; H2_Str = ""; H4_Str = ""; H3_Str = "-#-";
                            }
                        } else if (N4_Str.equals("-#-")) {
                            VHOL = VHSHAT;
                            if (XDAM < 2) {
                                VHDAM = VHSHATMAX;
                                H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                            }
                        } else {
                            // DITTO FOR // VLTRU// <>// VLND//
                            VHOL = VHSHAT; VHDAM = VHSHAT;
                            if (H1_Str.equals("-#-")) {
                                // LID if IMPACT & POST-IMPACT DO NOT USE // VLND//
                                H1_Str = ""; H3_Str = ""; H4_Str = ""; H2_Str = "-#-";
                                if (VHSHAT == VHSHATMAX) {
                                    H2_Str = ""; H3_Str = "-#-";
                                }
                            } else {
                                // LID if POST-IMPACT USES // VLND//
                                H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                                if (VHSHAT == VHSHATMAX) {
                                    H2_Str = ""; H3_Str = "-#-";
                                }
                            }
                        }
                    }
                } else {
                    // HBL LID FOR ALL PROJ (INCLUDING BRITISH DEFORMABLE PROJ) AT ALL obliquity
                    if (VHOL >= VHSHAT) {
                        if (VHDAM < VHOL) {
                            if (VHDAM > VHSHATMAX) {
                                VHDAM = VHSHATMAX; XDAM = 1;
                            } else {
                                XDAM = 2;
                            }
                        } else {
                            XDAM = 0;
                        }
                        if (H4_Str.equals("-#-") || (H4_Str.equals("-#-") && VLSHAT == VLSHATMAX)) {
                            VHOL = VHSHATMAX;
                            if (XDAM == 2) {
                                if (H4_Str.equals("-#-")) {
                                    H1_Str = ""; H2_Str = ""; H3_Str = "--"; H4_Str = "-#-";
                                }
                            } else {
                                VHDAM = VHSHATMAX;
                                H1_Str = ""; H2_Str = ""; H4_Str = ""; H3_Str = "-#-";
                            }
                        } else if (H4_Str.equals("-#-")) {
                            VHOL = VHSHAT;
                            if (XDAM < 2) {
                                VHDAM = VHSHATMAX;
                                H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                            }
                        } else {
                            // DITTO FOR VLTRU!=VLND
                            VHOL = VHSHAT; VHDAM = VHSHAT;
                            if (H1_Str.equals("-#-")) {
                                // LID if IMPACT & POST-IMPACT DO NOT USE VLND
                                H1_Str = ""; H3_Str = ""; H4_Str = ""; H2_Str = "-#-";
                                if (VHSHAT == VHSHATMAX) {
                                    H2_Str = ""; H3_Str = "-#-";
                                }
                            } else if (H1_Str.equals("--")) {
                                // LID if POST-IMPACT USES VLND
                                H1_Str = ""; H4_Str = ""; H2_Str = "--"; H3_Str = "-#-";
                                if (VHSHAT == VHSHATMAX) {
                                    H2_Str = ""; H3_Str = "-#-";
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // NORMAL SHATR
            // NOSE-ONLY SHATTER OCCURS WHEN CURVE-PLATE RULE || SOFT AP CAP USED AGAINST SOFTSHAT PLATE
            // exitAngle VEL RATIO FORMULA USED if VEL>=VHSHAT if SHATR W/O SPECIAL JAP CAP-HEAD EFFECTS
            VLMT = VLSHAT; VLDAM = VLSHAT; N2_Str = "-#-";
            VHOL = VHSHAT; VHDAM = VHSHAT; H2_Str = "-#-";
            if (VHSHAT == VHSHATMAX) {
                N2_Str = ""; N3_Str = "-#-"; // BEST POSSIBLE SHATR RESULT
                H2_Str = ""; H3_Str = "-#-";
            }
            if (strikingVelocityFPS >= VHOL && strikingVelocityFPS < VLMT) { penetrationFlag = PENFLAG_HOLING; }// PARTIAL SHATRD PEN if // penetrationFlag//  = 1
        }
        // *
        // * FINAL penetrationFlag & exitAngle CALC LOGIC *
        // if NO SHATR, exitAngle LINEARLY INCREASES FROM 0 TO exitAngleMIN AS VEL INCREASES FROM HBL TO NBL.
        // ALWAYS USE DESIGNATED LIMITS FOR THAT POST-IMPACT PROJ DAMAGE LEVEL.
        // *
        // if VEL<HBL, NO LARGE HOLE MADE IN PLATE & exitAngle IS UNDEFINED, SO EXIT exitAngle LOGIC
        VRATMIN = VLMT; //  DEFAULT VALUE FOR exitAngle COMPUTATION IN DEFLECTION SUBROUTINE
        if (SHAT  && proj.HARD != 2 && strikingVelocityFPS >= VHSHAT) { VRATMIN = VHOL; } //  SHATR REPLACEMENT VALUE
        if (strikingVelocityFPS < VHOL) {
            VRAT = -1; penetrationFlag = PENFLAG_NO_LARGE_HOLE;
            exitAngle = -1; OBDF = -1;
            // MUST ALWAYS SET // OB45//  FOR REVERSE exitAngle CALC IN EFFCTV PROJ LIMIT LOGIC
            if ((obliquity - exitAngleMIN) > 45) {
                OB45 = ((obliquity - exitAngleMIN) - 45) / 45f; // OB45 = FRACTION OF (obliquity - exitAngleMIN) > 45 DEG
            } else {
                OB45 = 0; // DEFAULT (=<45 DEG) VALUE RESTORED
            }
            return;
        }

        // VEL RATIO STARTS WHEN VEL==VLMT if NO SHATR or if HARD-CAPPED SHATR.
        //   if REGULAR SHTR, then VEL= VHOL
        if (!SHAT || (proj.HARD == 2)) {
            // UNCAPPED JAP TYPE 91 AP W/ strikingVecloity<=VHSHAT ACTS LIKE UNSHATRD PROJ
            if (strikingVelocityFPS >= VHOL && strikingVelocityFPS < VLMT) {
                // BETWEEN HBL & NBL & NO SHATR
                EXNBL = exitAngleMIN;
                penetrationFlag = PENFLAG_HOLING;
                exitAngle = exitAngleMIN * (strikingVelocityFPS - VHOL) / (VLMT - VHOL); //  LINEAR INCREASE IN // exitAngle//
                // MUST ALWAYS SET // OB45//  FOR REVERSE // exitAngle//  CALC IN EFFCTV PROJ LIMIT LOGIC
                if ((obliquity - exitAngleMIN) > 45) {
                    OB45 = ((obliquity - exitAngleMIN) - 45) / 45f; // // OB45//  = FRACTION OF (obliquity - exitAngleMIN) > 45 DEG
                } else {
                    OB45 = 0; // DEFAULT (=<45 DEG) VALUE RESTORED
                }
                OBDF = obliquity - exitAngle; // DEFLECTION ANGLE

                return;
            }
        } else {
            // BETWEEN HBL & NBL W/SHATR
            if (strikingVelocityFPS >= VHSHAT && strikingVelocityFPS < VLSHAT) { penetrationFlag = PENFLAG_HOLING; }
        }

        // NO SHTR COMPLETE PEN || SHTR HOLING LOGIC
        if (SHAT || obliquity > 15) {
            // exitAngle VEL RATIO FORMULA
            // FOR COMPLETE PEN AT obliquity > 15 DEG W/O SHATR or ALL HITS AT VEL >= HBL W/SHATR
            // exitAngle -> obliquity (DEFLECTION = obliquity - exitAngle = OBDF -> 0) AS VEL INCREASES (ASSUMES PROJ DAMAGE HELD CONSTANT)
            //   IN A CONTINOUSLY-SLOWING (ASYMPTOTIC) RATE.
            //  OBDF IS FOUND BY SOLVING;
            // (SEE DEFINITIONS ABOVE & BELOW)
            //    SIN(OBDF)*COS(OBDF) = SNCSMAX/[VRAT^2 + VRAT*SQR(VRAT^2 - 1)]
            // if (obliquity - exitAngleMIN) IS OVER 45 DEG { ADD FRACTION OF (obliquity - exitAngleMIN) OVER 45 DEG TO OBDF
            if (obliquity <= .005f) {
                exitAngle = 0;
                EXNBL = 0; // PREVENT DIVIDE-BY-ZERO ERROR FOR SHTR ONLY
            } else {
                VRATVEL = strikingVelocityFPS;
                OB45 = calculateDeflection();

                // ACTUAL exitAngle (ALWAYS >= exitAngleMIN)
                exitAngle = obliquity - TMPOBDF;
                EXNBL = exitAngleMIN;
                if (SHAT) {
                    VRATVEL = VLSHAT;
                    calculateDeflection();
                    EXNBL = obliquity - TMPOBDF; // 'exitAngle' AT NBL FOR 'VR' CALC ONLY
                }
            }
        } else {
            // if NO SHATR & obliquity <= 15 DEG, { NO DEFLECTION if VEL >= NBL
            exitAngle = EXNBL = obliquity;
        }
        OBDF = obliquity - exitAngle; // RADIANS FOR CALC & DEFLECTION ANGLE
    }




    //
    // 'TYPICAL' PROJ NOSE & BODY DAMAGE LOGIC *
    void calculateDamageToProjectile() {

        //INIT VARIABLES
        BDYDM = 0; BRK = 0; NSBRK = 0;


        useCurveRule =  (penetrationFlag == PENFLAG_HOLING && curveFlag); // SET CURVED-PLATE RULE IN FORCE FLAGS

        // SHATRD PROJ DAMAGE LOGIC
        if (SHAT)
            SHATRDAM();
        else {

            //BEGIN UNSHATRD PROJ LOGIC
            //
            //UNSHATRD PROJ COMPLETE PEN NOSE & BODY DAM LOGIC
            //
            //* NOSE DAMAGE CRITICAL obliquity MOD CALC *
            //
            NOSEDAM();


            //ONLY COMPUTE NOSE DAMAGE USING CRITICAL NOSE (obliquity - exitAngle) LOGIC IF VEL >= HBL ('penetrationFlag'>0),
            //  WHICH RELIEVES FORCE ON NOSE DUE TO INITIAL IMPACT, BUT ADDS TO TWISTING FORCES ON NOSE
            //
            if (penetrationFlag < PENFLAG_COMPLETE) {
                //
                //ALL FH PLATES USUALLY CAUSE NOSE DAMAGE (NOT SHATR HERE) IF VEL < NBL WHEN ANY OF THESE CONDITIONS APPLY:
                // (1) PROJ BODY IS WEAK/BRITTLE (IMPLIES NOSE IS BRITTLE, TOO) & BREAKS IF IT DOES
                //     NOT COMPLETELY PENETRATE A PLATE ('BRAAK' is true)--INCLUDES ALL CHILLED CAST IRON PROJ
                //    ('SHATRES' = jFacehard.Projectile.SHATRES_VERYLOW)
                // (2) PROJ HAS A NO AP CAP ('HARD' = 0) OR IT HAS ONLY A HOOD ('HARD' = -1) (LOW-GRADE SOFT AP CAP) &
                // HAS A WEAK BODY ('SHATRES' = jFacehard.Projectile.SHATRES_LOW)
                //     & IT HITS ARMOR OTHER THAN TYPE_COMPOUND ARMOR (not 'armor.isCompound')
                // (3) PLATE IS AN EXTRA-TOUGH (MOST POST-WWI) PLATE ('SOFTSHAT' = SHATTER_SOFTCAPPED_PROJ) & PROJ HAS A HOOD OR A
                // SOFT AP CAP ('HARD' = -1 OR 1)
                // (4) obliquity > 'NDAP' (MODIFIED CRITICAL NOSE DAMAGE (obliquity - exitAngle) THRESHOLD) & HITS WITH 'strikingVelocityFPS' < HBL ('penetrationFlag' = 0)
                // (5) HOLING PEN ('penetrationFlag' = 1) & (obliquity - exitAngle) > 'NDAP' REGULAR LOGIC (ALSO USED FOR COMPLETE PEN)
                // (6) obliquity > 45 DEG NO MATTER WHAT
                //
                //'NSBRK' INCOMPLETE PEN FLAG LOGIC
                //
                NOSEBROKE();
                //
            } else {

                //* NOSE DAMAGE CRITICAL obliquity CALC FOR COMPLETE PEN *
                if ((obliquity - exitAngle) > NDAP)
                    NSBRK = 3; // FLAG SET TO PRINT "PROJ NOSE BROKEN" MESSAGE
            }
        }


        if (VITRU == -1) {

            // LOWER BODY DAMAGE OCCURS USING CRITICAL ANGLE FORMULA TO U.S. WWII STANDARD-TYPE PROJ
            // IF VEL >= HBL & CURVED-PLATE RULE IS NOT IN FORCE
            // ONLY APPLY LOWER BODY TEST IF NOSE IS BROKEN OR COMPLETE PENETRATION
            // OCCURRED (LOWER BODY HITS PLATE, TOO)

            DOCRTGOOD();

            calculateProjectileBendingAndBreakage();

        } else
            if (strikingVelocityFPS < VITRU && BRK == 0 && penetrationFlag == PENFLAG_COMPLETE)
                BRK = 3; // BREAK PROJ IF REGULAR BREAK LOGIC APPLIES & PROJ HITS AT TOO LOW A VEL


        if (penetrationFlag < PENFLAG_COMPLETE && BRK == 0) {
            //RICOCHET & HOLING-ONLY PEN LOGIC (SKIP IF COMPLETE PENETRATION)
            //W/O CURVED-PLATE RULE, FOR STEEL PROJ THAT ALWAYS BREAK AT VEL<NBL AGAINST ALL-STEEL ARMOR,
            //  TYPE_COMPOUND PLATES CAUSE THIS ONLY IF VEL<HBL
            if (BRAIK && (!armor.isCompound ||
                           proj.SHATRES == Projectile.SHATRES_VERYLOW ||
                           penetrationFlag == PENFLAG_NO_LARGE_HOLE)
               )
                BRK = 5; // ALL PROJ INEFFECTIVE AT obliquity > 40 (50 IF TYPE_COMPOUND ARMOR) DEG IF VEL<HBL & PLATE IS FLAT
            else if (obliquity > armor.OBRK() && (penetrationFlag == PENFLAG_NO_LARGE_HOLE || proj.SHATRES != Projectile.SHATRES_HIGH))
                BRK = 6; // MAX EFFECTIVE obliquity RULE
            else if ((VLMT > VLND) || (VITRU >= VLMT))
                BRK = 3; //  BREAK PROJ AT UNDER 'VLMT' IF 'VLMT' IS OTHER THAN THE UNDAMAGED NBL

        }

        // GOOD PROJ DO NOT BREAK IF THEY HOLE, BUT NOT COMPLETELY PEN, A CURVED PLATE
        //  OR IF HOOD OR SOFT AP CAP WORKS AGAINST A 'SOFTSHAT' = SHATTER_SOFTCAPPED_PROJ PLATE
        if ((useCurveRule && proj.SHATRES == Projectile.SHATRES_HIGH) ||
                (armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ && NSSHAT == 1)) {
            BRK = 0;
            if (SHAT && NSSHAT == 0)
                NSSHAT = 3; // ONLY NOSE BREAKS IF SHATR WITH CURVED-PLATE RULE
        }

        if (BRK > 0) {
            if (proj.SHATRES == Projectile.SHATRES_VERYLOW) {
                if (NSBRK == 0)
                    NSBRK = 1; // SET IF NOT ALREADY SET
                BDYDM = 2; // IF CHILLED CAST IRON PROJ LOWER BODY BREAKS, ALL OF IT BREAKS.
            } else {
                if (BDYDM == 0)
                    BDYDM = 1; // BODY RENDERED INEFFECTIVE. HOWEVER, DAMAGE MIGHT NOT EFFECT FURTHER PEN IN SOME CASES.
            }
        }


    }





    //
    //* TALLY FINAL PEN RESULTS *
    //WHEN ANY U.S.-STANDARD-TYPE PROJ COMPLETELY PEN, THEY ALWAYS ARE ASSUMED TO BE ABOVE THE 'QE'-CALCULATED EFFECTIVE LIMIT (NO MAJOR BODY DAMAGE)
    //THE DAMAGE COMPUTATIONS FOR U.S.-STANDARD-TYPE PROJ (CRITICAL BODY & NOSE DAMAGE) DON'T MODIFY PEN INTO PLATE CAUSING DAMAGE, SO ANY COMPLETE PEN IS ASSUMED,
    //  AS W/'QE'-USING PROJ WHEN VEL >= 'VITRU', TO BE "PERFECT" HERE.
    //THESE FORMULAE DETERMINE WHETHER PEN IS ACHIEVED, WHAT KIND OF PEN, POST-IMPACT CONDITION OF PROJ, & ITS REMAINING (EXIT) VEL IF IT (OR PIECES OF IT) PASS THRU PLATE.
    //ALWAYS USE 'VHDAM' & 'VLDAM' FOR THESE COMPUTATIONS, SINCE ABOVE EFFECTIVE LIMIT, ENERGY LOST DURING PEN IS REDUCED
    final void calculateFinalResults() {

        if (strikingVelocityFPS < VHOL) {
            //NEXT LINES GIVE EFFECTS IF NO HOLE MADE COMPLETELY THRU PLATE
            //'penetrationType = PENETRATION_NONE' MEANS THAT ONLY SHOCK EFFECTS OCCUR BEHIND PLATE UNLESS NEXT LINE
            //    MODIFIES THIS TO 'penetrationType = PENETRATION_NONE_SPLINTERS FOR SPLINTERS BEING KNOCKED FROM PLATE BACK
            //* NO PEN THRU PLATE ACHIEVED (EXCEPT FOR SMALL POSSIBLY BACK-SPALLING AND A SMALL HOLE IF SHATR OCCURRED NEAR HBL) *
            penetrationType = PENETRATION_NONE;
            if (NORMPLUGWT <= 0 && armor.effectiveInchesOfMetalBacking() <= 0 && CART != Armor.CARTWHEEL_BACKSPALL_NO)
                penetrationType = PENETRATION_NONE_SPLINTERS; // NO BIG HOLE BUT BRITTLE PLATE SPRAYS FRAGMENTS UNLESS BACKING MATERIALS STOP THEM

            TOTPLUGWT = 0; NORMPLUGWT = 0; DELTAPLUGWT = 0; VR = -1; VNPLUG = -1;
            VDPLUG = -1; exitAngle = -1;  OBDF = -1; HF = -1; VRPR = -1;
            return;
        }


        //* SOME FORM OF PEN ENTIRELY THRU PLATE ACHIEVED (VEL >= HBL) *
        //FACE IS CRACKED THRU AT A VEL < 'VHOL', THEN SLOWS PROJ DOWN BY CONSERVATION OF MOMENTUM,
        //   THEN ADDS ITSELF TO PROJ NOSE DURING PUSHING OUT OF NORMAL PLUG THRU BACK LAYER.
        //   ONCE PUNCHED OUT, BACK LAYER NOW ALSO ADDS TO PROJ NOSE TO FORM NORMAL PLUG & ENTIRE MASS IS SLOWED TO
        //   STOP IF VEL <= HBL (AT HIGH OBLIQUITY, PROJECTILE WILL RICOCHET AT AN ANGLE AS ITS SIDEWAYS VELOCITY
        //   PORTION IS NOT REDUCED).  AT HIGHER VEL, NORMAL PLUG KEEPS GOING AFTER PROJ SLOWS DOWN TO STOP OR RICOCHETS
        //   IF VEL < NBL.  THEREFORE, PUSH THRU BACK LAYER IS AN *INELASTIC* COLLISION W/PROJ & NORMAL PLUG IN ONE
        //   PIECE AT HBL.  MAX NORMAL PLUG VEL IS AT MOMENT OF EJECTION UNDER FORCE OF PROJ NOSE &, AT HIGH OB, BODY.
        //   ONLY COMPONENT OF FORCE DIRECTLY INTO PLATE HAS ANY EFFECT ON NORMAL PLUG VEL.
        //REMAINING VEL CALC NEEDS TO REMOVE NORMAL PLUG ENERGY & ENERGY LOST TEARING THRU PLATE BETWEEN HBL & NBL.

        KETOTAL = .5f * proj.bodyWeightInPounds * strikingVelocityFPS * strikingVelocityFPS; // ENERGY AVAILABLE (AP CAP & WINSCREEN DO NOT PEN)
        KEPUNCH = .5f * proj.bodyWeightInPounds * VHDAM * VHDAM; // ENERGY NEEDED TO PUNCH OUT NORMAL PLUG AT OB (BASE SLAP ENLARGES PLUG W/O INCREASING HBL)
        KE1 = KETOTAL - KEPUNCH; // ENERGY LEFT TO SPLIT BETWEEN PROJ & PLUGS
        V1 = Util.sqr(2f * KE1 / proj.bodyWeightInPounds); // VEL EQUIVALENT OF KE1
        VNPLUG = Util.sqr(proj.bodyWeightInPounds / (proj.bodyWeightInPounds + NORMPLUGWT)) * V1 * Util.cos(obliquity); // NORMAL PLUG VEL (FUSED W/PROJ IN ONE LUMP WHEN PLUG FORMED)
        VSPR = VNPLUG / Util.cos(obliquity); // NORMAL COMPONENT OF PROJ VEL SAME AS NORMAL PLUG VEL WHEN PLUG EJECTED
        KEVSPR = .5f * proj.bodyWeightInPounds * VSPR * VSPR; // PROJ ENERGY PRIOR TO TRYING TO PASS THRU PLATE
        //DEFLECTION OF PROJ (= obliquity - exitAngle) SLOWS IT DOWN FURTHER (BOUNCES OFF SIDES OF HOLE DURING COMPLETE PENETRATION)

        float tmp1 = Util.sin(obliquity);
        float tmp2 = Util.sin(exitAngle);

        KEOBMNSEX = .5f * proj.bodyWeightInPounds * VSPR * VSPR * ((tmp1 * tmp1) - (tmp2 * tmp2));
        //PROJ ENERGY LOST DUE TO DEFLECTION (COMPONENT PARALLEL TO PLATE FACE)
        KE2 = KEVSPR - KEOBMNSEX; // PROJ ENERGY AFTER DEFLECTION
        V2 = Util.sqr(2f * KE2 / proj.bodyWeightInPounds); // VEL EQUIVALENT OF KEHBL

        if (BRK > 0)
            BFRACT = .5f; // HALF OF BROKEN PROJ ASSUMED TO PEN BETWEEN HBL & NBL IF PARTIAL PEN SET ('penetrationType'=PENETRATION_SEMI_UPPER_BODY @ 'OB'<45 DEG OR 'penetrationType'=PENETRATION_SEMI_LOWER_BODY OTHERWISE)
        else if (NSBRK > 0)
            BFRACT = .667f; // ONLY 1/3 OF PROJ PIECES (OF NOSE) PEN BETWEEN HBL & NBL IF PROJ NOSE BREAKS OR SHATTERS ('penetrationType'=PENETRATION_HOLE_REJECTED_DAMAGED_BODY @ 'OB'<45 DEG)
        else
            BFRACT = 1; // IF NO NOSE DAMAGE & NO BODY BREAKAGE, ENTIRE PROJ (MINUS AP CAP & WINDSCREEN) PEN AT NBL ONLY


        //'VRSHATNS' = VEL OF PENETRATING BROKEN-UP NOSE PIECES AT LOW obliquity OR BROKEN-UP LOWER BODY PIECES AT HIGH obliquity BETWEEN HBL & NBL
        VRSHATNS = V2 * Util.sqr(proj.bodyWeightInPounds / (proj.bodyWeightInPounds + (1 - BFRACT) * DELTAPLUGWT)); // BOTH EQUAL IF NO DELTAPLUG

        if (strikingVelocityFPS < VLMT)
            VRPR = -1; // REMAINING VEL 'VRPR' NOT DEFINED BELOW NBL
        else {
            if (SHAT)
                VDFCALC = SHATVDFPR;
            else
                VDFCALC = VDFUSEDPR;

            //PROJ REMAINING VEL CALC ABOVE NBL ONLY
            //'VRPR' = PROJ REMAINING VEL (BASE PIECES ONLY IF SHATR) AT AND ABOVE NBL (NOT DEFINED IF strikingVelocityFPS < NBL)
            //CALCULATE ADDITIONAL PROJ ENERGY NEEDED TO REACH NBL
            TOPVEL = (1 / (1 - VDFCALC)) * VHDAM;
            if (VLDAM < TOPVEL) TOPVEL = VLDAM; // NBL ENERGY LOSS VEL
            float keNBL = .5f * proj.bodyWeightInPounds * TOPVEL * TOPVEL; // TOTAL ENERGY LOST AT NBL (AP CAP & WINSCREEN DO NOT PEN)
            float ke1NBL = keNBL - KEPUNCH; // ENERGY LEFT TO SPLIT BETWEEN PROJ & PLUGS
            float v1NBL = Util.sqr(2f * ke1NBL / proj.bodyWeightInPounds); // VEL EQUIVALENT OF ke1NBL
            float VNPLUGNBL = Util.sqr(proj.bodyWeightInPounds / (proj.bodyWeightInPounds + NORMPLUGWT)) * v1NBL * Util.cos(obliquity); // NORMAL PLUG VEL (FUSED W/PROJ IN ONE LUMP WHEN PLUG FORMED)
            float VNBLPR = VNPLUGNBL / Util.cos(obliquity); // NORMAL COMPONENT OF PROJ VEL SAME AS NORMAL PLUG VEL WHEN PLUG EJECTED
            float KENBLPR = .5f * proj.bodyWeightInPounds * VNBLPR * VNBLPR; // PROJ ENERGY AT NBL PRIOR TO TRYING TO PASS THRU PLATE
            //DEFLECTION OF PROJ (= obliquity - exitAngle) SLOWS IT DOWN FURTHER (BOUNCES OFF SIDES OF HOLE DURING COMPLETE PENETRATION)
            float KENOME = .5f * proj.bodyWeightInPounds * VNBLPR * VNBLPR * ((tmp1 * tmp1) - (tmp2 * tmp2));

            //PROJ ENERGY LOST DUE TO DEFLECTION (COMPONENT PARALLEL TO PLATE FACE)
            float KE2NBL = KENBLPR - KENOME; // PROJ ENERGY AT NBL AFTER DEFLECTION
            V2NBL = Util.sqr(2f * KE2NBL / proj.bodyWeightInPounds); // VEL EQUIVALENT OF KE2NBL
            //SUBTRACT NBL ENERGY FROM PROJ ENERGY
            VRPR = Util.sqr((proj.bodyWeightInPounds / (proj.bodyWeightInPounds + BFRACT * DELTAPLUGWT)) * (V2 * V2 - V2NBL * V2NBL)); // REMAINING VELOCITY FOR PROJ & DELTA PLUG
        }

        //DETERMINE 'PENTYP' (=2-6) & CALC 'VR', 'VDPLUG', & 'VNPLUG'
        calculatePenetrationTypeAndResidualVelocities();

    } // calculateFinalResults



    void NOSEBROKE() {

        //'NSBRK = 1' SET LOGIC

        if (NSBRK == 0) {
            //IF SHATR 'NSBRK' = 2 ALREADY SET, IGNORE THIS LOGIC
            if (BRAIK)
                NSBRK = 1;
            else if (proj.HARD == -1 && proj.SHATRES == Projectile.SHATRES_LOW && !armor.isCompound)  // TODO: check this last armor.isCompound thing vs jFacehard.Impact
                NSBRK = 4; //  HOODED COMMON PROJ NOSE BREAKAGE
            else if (proj.softCapOrHood() && armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ)
                NSBRK = 5; //  HOODED OR SOFT-CAPPED FULL AP PROJ NOSE BREAKAGE REQUIRES EXTRA-TOUGH PLATE
            else if (penetrationFlag == PENFLAG_NO_LARGE_HOLE && obliquity >= NDAP)
                NSBRK = 3; //  NO HOLE 'NDAP' CHECK
            else if (penetrationFlag == PENFLAG_HOLING && obliquity - exitAngle > NDAP)
                NSBRK = 3; //  HOLING 'NDAP' CHECK
            else if (obliquity > 45)
                NSBRK = 6;
        }

    } // NOSEBROKE


        // NOSE DAMAGE CRITICAL obliquity MODIFICATION AND RESULTS CALC *
    void NOSEDAM() {
        NDAP = proj.noseDamageAngle; // 'proj.noseDamageAngle' VALUES ARE FOR AVERAGE WWI ARMOR, SO MODIFY THEM FOR OTHER KINDS OF ARMOR
        if (armor.isCompound)
            NDAP +=  10; // TYPE_COMPOUND ARMOR IS WEAK
        if (armor.isThinlyFaced)
            NDAP += 5; // THIN CHILL ARMOR IS ALSO WEAK, BUT NOT AS BAD
        if (armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ)
            NDAP -= 10; // 'SOFTSHAT' ARMOR IS MUCH BETTER THAN AVERAGE WWI ARMOR
        if (armor.SOFTSHAT == Armor.SHATTER_WEAK_SOFTCAPPED_PROJ)
            NDAP -= 5; // BRITISH WWI-ERA CA SHATTERS SOFT-CAPPED AP EXCEPT MIDVALE UNBREAKABLE
        if (NDAP < 5 && proj.noseDamageAngle > 0)
            NDAP = 5; // USE A 5-DEGREE MIN ANGLE > 0
        if (NDAP < 0)
            NDAP = 0; // MIN ANGLE = 0

    } // NOSEDAM




    // BRITISH DEFORMABLE PROJ ('proj.canBend' = true) PENETRATION RULES
    void calculateProjectileBendingAndBreakage() {
        if (proj.canBend && !SHAT) {
            if (obliquity >= criticalObliquity) {
                if (proj.CARDONALD == Projectile.CARDONALD_CARDONALD) {
                    //IF CARDONALD PROJ COMPLETELY PENETRATES AT ABOVE CRITICAL VEL, IT IS INTACT
                    if (strikingVelocityFPS >= VSCRIT)
                        BRK = 0;
                    else
                        BRK = 7;
                } else
                    BRK = 8; // ONLY CARDONALD PROJECTILES RECOVER FROM SHATTER-BREAKAGE HERE

            } else {
                if ((!BRAIK && penetrationFlag < PENFLAG_COMPLETE) || penetrationFlag == PENFLAG_COMPLETE)
                    BRK = 0; //  PROJ BODY DAMAGE NIL BELOW 'criticalObliquity' ON COMPLETE PENETRATION OR WITH STRONG PROJ
                else
                    BRK = 9; //  WEAK ('BRAIK' is true) PROJ BODY BREAKS IF NOT COMPLETE PENETRATION

            }
        }

    } //   calculateProjectileBendingAndBreakage




    //
    // COMPUTE VELOCITY HOLING DIFFERENTIAL TO USE IN THIS CASE
    //
    //  BRITTLE WWI PLATES (ALL 'THKTHN = BRITTLE' PLATES) USE 'VDFSTDWW1';
    //  ALL MORE MODERN, TOUGHER ('THKTHN > BRITTLE') PLATES USE SMALLER 'VDFSTDWW2' DIFFERENCE.
    //  BRITISH DEFORMABLE PROJECTILES ('proj.canBend' = true) VARY THIS FROM A NARROW GAP UP TO 22.5 DEG
    //  THEN A RAPID INCREASE IN THE GAP UNTIL IT REACHES THE STANDARD PLATE GAP AT & ABOVE 45 DEG.
    //  A CARDONALD PROJ IS MORE RIGID AND DOES NOT SUFFER THIS RAISING OF THE HBL (AT LEAST AS I SEE IT NOW).
    //  NON-BRITISH-CPC-TYPE PROJECTILES THAT BREAK IF THEY DO NOT COMPLETELY PENETRTATE HAVE HBL RAISED BY 'SHATVDF'
    //  SOFT-CAPPED BRITISH-CPC-TYPE PROJECTILES ALWAYS USE THE WWII VDF VALUE (THE GAP BETWEEN HBL & NBL STAYS LARGE)
    //  IF SHATTER OCCURS OR 'VDFBRK' IF NO SHATTER (CALCULATED IN SUBROUTINE 'CALCNBL' IN 'FH40MAIN' MODULE)
    //



    //TOUGHER WWII & BRITISH POST-1911/WITKOWITZER WWI ARMORS OR BRITISH-CPC-TYPE PROJ

    void calculateVelocityHolingDifferential() {

            // VARIABLE 'VDFSTD' USED FOR NON-DEFORMING PROJ & BRITISH 'CARDONALD' PROJ WITH 'VLND' & 'VHND'
        VDFSTD = VDFSTDWW1; //  BRITTLE WWI ARMOR VEL GAP BETWEEN HBL & NBL
        // TODO: the above line was ABOVE this function it seemed... double check?

        if (armor.THKTHN > Armor.THKTHN_BRITTLE ||
                (BRAIK && proj.LTCASE == Projectile.LTCASE_MEDIUMCAVITY && proj.softCapOrHood()))
            VDFSTD = VDFSTDWW2;

        VDFUSED = VDFSTD; //  DEFAULT FOR MOST NON-BRITTLE PROJ (CONSTANT FOR ALL 'OB')

        if (proj.canBend && proj.CARDONALD == Projectile.CARDONALD_NO) {
            // BRITISH DEFORMABLE NON-CARDONALD PROJ
            float OBVDF = obliquity;
            if (obliquity < 22.5)
                OBVDF = 22.5f; //  EQUAL TO 'DIF2' IF OB =< 22.5 DEG
            if (obliquity > 45)
                OBVDF = 45f; //  EQUAL TO 'DIF1+DIF2' IF OB >= 45 DEG

            float VDFVAL = 90f * (2 * ((OBVDF - 22.5f) / 22.5f) + 1);
            DIF1 = .08f;
            float DIF2 = .01f; //  NON-CARDONALD DEFORMING PROJ THAT ARE NOT AS BRITTLE
            VDFBND = (VDFUSED / (DIF1 + DIF2)) * (DIF1 * (1f - Util.sin(VDFVAL)) / 2 + DIF2);
        } else
            VDFBND = 0; // NON-BENDING PROJECTILES OR BRITISH CARDONALD BENDING PROJECTILES

    } // calculateVelocityHolingDifferential



    // CALCULATED DEFLECTION OF 'exitAngle' FROM ORIGINAL IMPACT 'obliquity'

    float calculateDeflection() {

        float OB45CALC = 0;

        VRAT = VRATVEL / VRATMIN;
        float TMPV = VRAT * VRAT - 1f;
        float TMPVEL = VRAT * (VRAT + Util.sqr(TMPV));
        float TMPDF1 = SNCSMAX / TMPVEL;
        float TMPDF2 = 1f - 4f * TMPDF1 * TMPDF1;
        float TANOBDF = (1f - Util.sqr(TMPDF2)) / (2 * TMPDF1); // TRIG IDENTITY FOR TANGENT OF 'OBDF'
        TMPOBDF = Util.atan(TANOBDF); // 'TMPOBDF' = 'exitAngleMIN' AT NL, SO EXTRA LOGIC BELOW NEEDED IF (obliquity - exitAngleMIN) > 45 DEG
        if ((obliquity - exitAngleMIN) > 45)
            OB45CALC = ((obliquity - exitAngleMIN) - 45) / 45; // OB45CALC' = FRACTION OF (obliquity - exitAngleMIN) > 45 DEG


        TMPOBDF *= (1 + OB45CALC); // 'OB45CALC' CHANGES IN STEP WITH 'OBDF' FOR 45 DEG, IF NOT ZERO

        return OB45CALC;
    } //  calculateDeflection **




    // INITIALIZE EFF VEL CALC VARIABLES
    public void initializeEffectiveVelocityVariables() {

        // TODO: figure where these variables should reside
        // some may belong here, but others
        MINEV = 0;  NSFLG = 0; CRITVEL = 0; NOSEVEL = 0; NVRFLAG = 0;
        MINEV1 = 0; MINEV2 = 0; MINEV3 = 0; MINEV4 = 0; MINEV5 = 0;

        //SET UP FOR REVERSE 'exitAngle' LOGIC
        if (SHAT)
            MAXDIFF = 0; // SHATR HAS NO LINEAR 'exitAngle' PART BELOW 'VLMT'
        else
        //LINEAR 'exitAngle' BELOW 'VLMT' OR WHEN 'obliquity'<=15 DEG
        if (obliquity > 15)
            MAXDIFF = 15; // DEFLECTION AT 'VLMT' WHEN 'obliquity'>15 DEG
        else
            MAXDIFF = obliquity; // NO DEFLECTION AT 'VLMT' WHEN 'obliquity'<=15 DEG

    }        //  initializeEffectiveVelocityVariables **




    //
    //* MIN EFFECT PEN VEL USING 'CRTAPR' LOGIC *
    public float APCRITICALV() {
        return calculateThresholdVelocity(CRTAPR, VLMT, VHOL);
    }


    //
    //* MIN NO NOSE DAMAGE VEL *
    public float NSCRITICALV() {

        float f = calculateThresholdVelocity(NDAP, VLMT, VHOL);
        // ITS NOSE BREAKS IF VEL < HBL ('BRAAK' is true)

        return (VHOL >= f) ?  VHOL : f; //  MIN EFF VEL TEST

    }


    public float effectiveThicknessInProjectileDiameters() {
        return armor.effectiveInchesThick() / proj.diameterInInches;
    }

    //
    // **** SUBROUTINES FOLLOW ****
    //
    //  CENTRAL B.L. CALC--DO THE MATH! *
    public void calculateBallisticLimits() {

        float scaleFactor;
        float PLM;
        float PDM;
        float penetrationBonus = 0f;

        // for computing scaling factor
        float coeffA, coeffB, coeffC;

        // COMPUTE SCALE FACTOR

        // SCALING FACTOR CONSTANTS BASED ON UNAFFECTED BACK PERCENTAGE OF ACTUAL PLATE

        // THICKNESS (STEP-FUNCTION APPROX='BACK'='UB')
        // THINNER BACK = LARGER SCALING EFFECTS FROM FACE & TRANSITION LAYER SHEARING & BRITTLE FRACTURE FAILURE
        // CONSTANTS 'AZ' & 'BZ' FOR COMBINED FACE & TRANSITION LAYERS & 'CZ' FOR SOFT BACK LAYER
        // 'proj.diameterInInches' IS PROJ DIAMETER

        if (armor.UB > 90) {
            // EXTRAPOLATED VERY-THIN-FACED PLATE (NO REAL PLATE TYPE)
            coeffA = 0; coeffB = 1; coeffC = 79;
        } else if (armor.UB > 75) {
            // HARVEY & U.S. POST-WWI BETHLEHEM THIN CHILL CLASS 'A'
            coeffA = 6.65E-07f; coeffB = 5.35f; coeffC = 78.5f;
        } else if (armor.UB > 67.5) {
            // BRITISH WWII CA
            coeffA = .00037f; coeffB = 3.23f; coeffC = 77.8f;
        } else if (armor.UB > 62) {
            // STANDARD (GERMAN WWI KC a/A = DEFAULT)
            coeffA = .003f; coeffB = 2.75f; coeffC = 77.7f;
        } else if (armor.UB > 52) {
            // GERMAN WWII KC n/A
            coeffA = .03f; coeffB = 2.1f; coeffC = 77;
        } else if (armor.UB > 30) {
            // U.S. WWII THICK CHILL CLASS 'A'
            coeffA = 1; coeffB = 1.25f; coeffC = 67;
        } else {
            // U.S. PRE-WWI MIDVALE NON-CEMENTED CLASS 'A'
            coeffA = 10.57f; coeffB = .80625f; coeffC = 17.26f;
        }

        // COMPUTE SCALING FACTOR TERM

        // SCALING FACTOR IS BASED ON UNAFFECTED BACK PERCENTAGE OF ACTUAL PLATE THICKNESS
        // (STEP-FUNCTION APPROX) THINNER BACK = LARGER SCALING EFFECTS FROM FACE & TRANSITION
        // LAYER SHEARING & BRITTLE FRACTURE FAILURE
        // 'coeffA' & 'coeffB' FOR FACE & TRANSITION LAYER & 'coeffC' FOR SOFT BACK LAYER
        scaleFactor = coeffA * (Util.pow(proj.diameterInInches,  coeffB)) + coeffC;


        float DEN = Util.pow(proj.totalWeightInPounds / (proj.diameterInInches * proj.diameterInInches * proj.diameterInInches), .2f); // PROJ WEIGHT TERM

        // THIN CHILL & TYPE_COMPOUND ARMORS CAUSE LESS DAMAGE, SO IF THEY CAUSE
        // DAMAGE, THE REDUCED 'QP' CAN BE INCREASED BACK UP TO 1.0 (HARD CAP)
        // OR 'proj.SOFTQPMAX()' (SOFT CAP) MAX ("PERFECT PROJ")

        if (armor.isCompound)
            penetrationBonus = .2f;
        if (armor.isThinlyFaced)
            penetrationBonus = .1f ;

        // 'apCapRemovedPenetrationBonus' INCREASE ADDED, IF USED
        penetrationBonus += proj.apCapRemovedPenetrationBonus; // BONUSES TO INNATE PROJ PEN ABILITY DUE TO WEAK PLATES & LOSS OF AP CAP (CAUSES SHATR, HOWEVER)

        // PROJ PEN QUALITY LOGIC
        if (proj.PLIM > 1 && proj.APCapType != proj.HARD)
            PLM = 1; //  IF proj.PLIM > 1, THE AP CAP IS THE REASON.  IF IT IS LOST, 'proj.PLIM' IS REDUCED TO DEFAULT MAXIMUM (1).
        else
            PLM = proj.PLIM - CAPHDLOSS;

        if (PLM < 1) {
            //NO BONUSES USED IF PROJ QUALITY ALREADY >= 1.00
            PLM += penetrationBonus;

            //BONUS CANNOT MAKE PROJ BETTER THAN ITS BEST POSSIBLE VALUE!!
            float maxPLM = (proj.HARD == 1 )? proj.SOFTQPMAX() : 1; // SOFT AP CAP ROBS ENERGY FROM IMPACT BUT CONTRIBUTES NO ENERGY
            if (PLM > maxPLM)
                PLM = maxPLM;
        }

        // PROJ DAMAGE-RESISTANCE QUALITY LOGIC
        if (proj.PLIM > 1 && proj.APCapType != proj.HARD) {
            //  LOSS OF THE AP CAP REDUCES 'PDAM' BY THE SAME AMOUNT AS 'PLIM'
            PDM = proj.PDAM * (1 - (proj.PLIM - 1) / proj.PLIM);
        } else
            PDM = proj.PDAM - CAPHDLOSS;

        // ONLY SOME BONUSES APPLY TO EFFECTIVE LIMIT
        if (PDM > 0 && PDM < 1)
            PDM += penetrationBonus;


        // SPECIAL BEST U.S. PROJ LOGIC FOR IMPACT EXTREME CONDITIONS
        // (NO IMPROVEMENT OVER PREVIOUS DESIGNS IF LOGIC APPLIES)
        if (proj.SPECIALEXTREMECONDITIONHANDLING) {
            float DAMCHK = (armor.effectiveShellShatteringInchesThick() / proj.diameterInInches) * obliquity;
            if (DAMCHK > 32.175f) {
                // USE NEXT-BEST PROJ (= 19) UNDER EXTREME CONDITIONS
               PLM = PDM = .94f;
               proj.criticalDamageAngle = proj.noseDamageAngle = 25;
            }
        }



        //'PNI' APPLIES TO EFFECTIVE LIMIT & 'PNL' TO HBL & NBL.
        //  BY DEFINITION, MIN POSSIBLE EFFECTIVE COMPLETE PEN IS AT NBL.
        float PNL = PLM;

        PENCONST = 1.9822E-06f * proj.diameterInInches * scaleFactor * DEN; // CONSTANT PEN FORMULA ('CORE' TERM)

        // CRITICAL DEFLECTION THRESHOLD ANGLE FOR "INEFFECTIVE" DAMAGE
        // NEGATIVE VALUES ONLY USED FOR BRITISH 'canBend' EFFECTIVE PROJ LOGIC
        if (proj.criticalDamageAngle <= 0)
            CRTAPR = 0;
        else
            CRTAPR = proj.criticalDamageAngle; // DEFAULT TABLE VALUE

        if (proj.criticalDamageAngle > 0 && proj.SHATRES != Projectile.SHATRES_VERYLOW) {
            // TYPE_COMPOUND PLATES CAUSE MUCH LESS DAMAGE TO STEEL PROJ
            if (armor.isCompound)
                CRTAPR = proj.criticalDamageAngle + 10;

            // THIN-FACED BETHLEHEM THIN CHILL & HARVEY PLATES CAUSE REDUCED DAMAGE TO STEEL PROJ
            if (armor.isThinlyFaced)
                CRTAPR = proj.criticalDamageAngle + 5;
        }

        //BRITISH WWII BENDING PROJ CRITICAL ANGLE 'criticalObliquity' INCREASES WITH OLDER PLATES
        if (proj.canBend)
            criticalObliquity = Util.abs(proj.criticalDamageAngle); //  strikingVelocityFPS WWII USUAL PLATE TYPES

        if ((armor.SOFTSHAT == Armor.SHATTER_NO_PROJ || CART != Armor.CARTWHEEL_BACKSPALL_NO) && !armor.isThinlyFaced) {
            if (armor.isCompound)
                criticalObliquity += 12; //  strikingVelocityFPS TYPE_COMPOUND ARMOR (ESTIMATE)
            else
                criticalObliquity +=  6; //  strikingVelocityFPS MORE BRITTLE NON-TYPE_COMPOUND PLATES (ESTIMATE)

        } else
            criticalObliquity = -1; //  NO-OP VALUE


        //COMPUTE OBLIQUE IMPACT THICKNESS MULTIPLIERS 'MO' & 'MSHAT'
        setObliquityMultipliers();

        //COMPUTE DAMAGE-RELATED OBLIQUE IMPACT & LIGHTCASE HBL, NBL, & EFFECTIVE LIMIT PROJ QUALITY MODIFIERS
        calculateProjectileQualityModifiers();

        float PNLPR; //'PNLPR' IS MAX PROJ QUALITY FACTOR ALLOWED.
                        // FOR USE WHEN VEL >= EFFECTIVE LIMIT FOR PROJ THAT ARE DAMAGED AT NBL
        if ((PNL < 1 && proj.HARD > 1) || proj.HARD == 0)
            PNLPR = 1; // BEST PERFORMANCE W/REGULAR HARD AP CAPS OR NO CAPS (LATTER HAS SHATR OVERRIDE THIS, OF COURSE)
        else if (proj.softCapOrHood())
            PNLPR = proj.SOFTQPMAX(); // BEST PERFORMANCE W/SOFT AP CAPS (THEY DON'T DAMAGE PLATES WHILE BEING DESTROYED)
        else
            PNLPR = PNL; // BEST PERFORMANCE FOR PROJ W/SUPERIOR HARD AP CAP DESIGN ('PNL' > 1)

        if (PNL > PNLPR)
            PNL = PNLPR; //  HIT THE MAX STOP VALUE
        PNI = (PNI < 0 || PNI > PNL) ? PNL : PDM; //  'PNI' CAN NEVER EXCEED 'PNL'

        // ADD THE EFFECTS OF ALL OTHER NORMAL-OB PROJ DAMAGE TO SHATR (LIGHTCASE PROJ ONLY)
        // NO OBLIQUE-IMPACT-ONLY EFFECTS USED SINCE SHATTER SCRAMBLES THEM
        // LOSS OF SOFT AP CAP OR HOOD INCREASES ENERGY AVAILABLE FOR PENETRATION SLIGHTLY,
        //  EVEN W/SHATR OCCURRING (NOT USED IF HARD CAP OR IF NO CAP OR HOOD TO START WITH)
        if ((proj.APCapType == Projectile.APCAP_HOOD || proj.APCapType == Projectile.APCAP_SOFT) &&
                proj.totalWeightInPounds == proj.bodyWeightInPounds && proj.LTCASE > Projectile.LTCASE_HEAVYCASE) {
            PNLSHAT = (PNL + (1f - proj.SOFTQPMAX())) * (1 - LCMOD) ;
            if (PNLSHAT > 1)
                PNLSHAT = 1; //  MAXIMUM SHTR PROJ QUALITY IS 1.00
            PSHMAX = 1;
        } else if (proj.LTCASE > Projectile.LTCASE_HEAVYCASE) {
            // LARGE CAVITY (4% VOLUME & UP) ALLOWS EXTRA DAMAGE EFFECTS ON TOP OF SHTR EFFECTS
            PNLSHAT = PNL * (1 - LCMOD);
            if (PNLSHAT > 1)
                PNLSHAT = 1; //  MAXIMUM SHTR PROJ QUALITY IS 1.00

            if ((proj.APCapType == Projectile.APCAP_HOOD || proj.APCapType == Projectile.APCAP_SOFT) && proj.totalWeightInPounds != proj.bodyWeightInPounds)
                PSHMAX = proj.SOFTQPMAX(); //  SOFT AP CAP OR HOOD STEALS ENERGY
            else
                PSHMAX = 1;

        } else {
            // SMALL CAVITY (UNDER 4% VOLUME) SEEMS TO PREVENT PROJ NON-SHATR DAMAGE IF SHTR OCCURS
            PNLSHAT = 1;
            PSHMAX = 1;
        }

        // IF THE PROJECTILE CHANGES ITS MODE OF FAILURE AS OBLIQUITY GOES UP IN THE 0-30 DEGREES RANGE
        //   FROM COMPRESSION/SHOCK DAMAGE TO BENDING DAMAGE, SHATTER SEEMS TO PREVENT THE BENDING DAMAGE
        //   FROM OCCURRING, SO THAT THE VALUE OF 'PLIM' AT OB = 0-15 DUE TO COMPRESSION/SHOCK DAMAGE GOES
        //   LINEARLY BACK TO 'PSHMAX' AT OB = 30 OR GREATER, REGARDLESS OF ITS VALUE AT OB = 0-15, IF UNDER 1!.
        if (proj.canBend && PNLSHAT < 1) {
            if (obliquity >= 30)
             PNLSHAT = PSHMAX;
          else if (obliquity > 15 && obliquity < 30)
              PNLSHAT = PNLSHAT + (PSHMAX - PNLSHAT) * (obliquity - 15) / 15;
        }

            // NO SEPARATE EFFECTIVE LIMIT EXISTS FOR SHATR
        if (SHAT)
            PNI = PNLSHAT;

        //*** COMPUTE ALL BALLISTIC LIMITS (NORMAL & AT GIVEN obliquity) ***

        //UNDAMAGED-PROJ NBL (USED FOR POST-IMPACT RESULTS IF DAMAGE CEASES AT SOME VEL > NBL; ONLY REAL IF NO DAMAGE AT NBL, TOO)
        VLND = (int) Util.pow(armor.totalEffectiveInchesWithBacking() * MO / (PENCONST * PNLPR), VXP);

        // COMPUTE DIFF BETWEEN NBL & HBL FOR VARIOUS CONDITIONS
        calculateVelocityHolingDifferential();

        float SHATMULT;

        //SHATR HBL WITH 20% OR, USUALLY, 30% PLATE THICKNESS SHATR BONUS AT NORMAL obliquity (LESS AT HIGHER obliquity)
        if (armor.SOFTSHAT == Armor.SHATTER_WEAK_SOFTCAPPED_PROJ && proj.softCapOrHood() && NSSHAT ==1  && obliquity <= 20)
            SHATMULT = 1.2f; // MAX SHATR BONUS FOR IMPROVED BRITISH WWI PLATES & REGULAR SOFT-CAPPED PROJ AT OB<=20 DEG
        else if (armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ && proj.HARD < 3 &&
                    !proj.canBend  && (NSSHAT == 0 || obliquity > 20))
            SHATMULT = 1.4f; // MAX SHATR BONUS FOR BEST SOFTSHAT PLATES WHEN PROJ HAS COMPLETE SHATR
        else
            SHATMULT = 1.3f; // MAX SHATR BONUS FOR EVERY OTHER CASE

        VHSHAT = (int) Util.pow(armor.totalEffectiveInchesWithBacking() * SHATMULT * MSHAT / (PENCONST * PNLSHAT), VXP);

        //'VHSHAT' IS THE IMPORTANT SHATRD-PROJ LIMIT, AS SOME PROJ PIECES PENETRATE THEN
        //'VLSHAT' MERELY ADDS SOME MORE PIECES IN MOST CASES, THOUGH FILLER MAY SOMETIMES ADD TO DAMAGE

        //DIFF BETWEEN SHATR HBL & NBL IS INCREASE OVER HBL THAT IS NEEDED TO COMPLETELY PENETRATE A PLATE
        if (obliquity <= 45)
            SHATVDF = 1f / (1f - VDFSTD); //  BENDING OF BRITISH DEFORMABLE NON-CARDONALD PROJ HAS NO EFFECT HERE
        else //DIFFERENCE BETWEEN SHATR HBL & NBL DECREASES AT OB > 45 DEG (OTHERWISE USE SAME VALUE)
            SHATVDF = 1f / (1f - (VDFSTD * Util.pow(Util.cos(2 * (obliquity - 45)), 8)));

        VLSHAT = (int)(SHATVDF * VHSHAT); // SHATR NBL DERIVED FROM SHATR HBL

        //"PERFECT" PROJ SHATR HBL & NBL W/NO OTHER DAMAGE EFFECTS. IGNORE BRITISH PROJ MODS HERE, TOO.
        //  THIS HBL USED AS UNSHATRD HBL WHEN 'VHSHATMAX' < 'VHTRU' DUE TO CONCENTRATION OF FORCE PUNCHING HOLE MORE EASILY.
        VHSHATMAX = (int) Util.pow(armor.totalEffectiveInchesWithBacking() *
                             SHATMULT * MSHAT / (PENCONST * PSHMAX), VXP); // NORMAL obliquity 'VHSHATMAX'

        VLSHATMAX = (int)(SHATVDF * VHSHATMAX); // BEST POSSIBLE SHATR NBL

        //ACTUAL HBL & NBL TAKING ALL PROJ DAMAGE (EXCEPT SHATR) FROM IMPACTED PLATE INTO ACCOUNT
        // UNSHATRD-PROJ NBL
        VLTRU = (int) Util.pow((armor.totalEffectiveInchesWithBacking() * MO / (PENCONST * PNL * POLMOD)) , VXP);


        // CALC HBL DERIVED FROM 'VLND', 'VLTRU' & (IN SOME CASES ONLY) 'VLSHAT'

        // DAMAGED-PROJ HBL ('VLTRU' IF NOT 'VHND') IS REPLACED BY 'VHSHAT' IF THIS HBL > 'VHSHAT'
        // UNDAMAGED-PROJ HBL ('VLND') IS REPLACED BY 'VHSHATMAX' IF THIS HBL > 'VHSHATMAX'
        // STANDARD WWI "15% THICKNESS DIFFERENCE RULE":  PLATE'S HBL = NBL OF A PLATE 15% THINNER;
        // WWII PLATES ARE USUALLY MUCH TOUGHER & HAVE A NARROWER DIFFERENCE.
        // AGAINST BRITISH DEFORMING PROJ, GAP IS NARROW TO 22.5 DEG & THEN WIDENS UNTIL REGULAR GAP USED ABOVE 45 DEG.
        // PROJ THAT BREAK IF THEY DO NOT COMPLETELY PENETRATE HAVE 'VDFBRK' > 0 EXCEPT BRITISH-CPC-TYPE
        // PROJ WHICH KEEP WWII-SIZE GAP BRITISH NON-CARDONALD DEFORMING ('VDFBND' > 0) PROJ MAY HAVE A NARROW
        // OR WIDE NBL-TO-HBL DIFFERENCE (ESTIMATE HERE)
        VDFUSEDPR = VDFUSED; SHATVDFPR = VDFUSED; // DEFAULT VALUES
        if (VDFBND == 0 && !BRAIK || proj.LTCASE == Projectile.LTCASE_MEDIUMCAVITY) {      // TODO: CHECK THIS PARENTHESIS USE
            VHTRU = (1 - VDFSTD) * VLTRU; // DEFAULT AVERAGE NBL-TO-HBL DIFFERENCE FOR STRONGER, NON-DEFORMABLE PROJ OR 'proj.LTCASE'=2 COLLAPSING PROJ BODY
            VHND = (1 - VDFSTD) * VLND;
        } else if (VDFBND > 0) {
        if (armor.UB > 45 && armor.UB < 70 &&
            CART == Armor.CARTWHEEL_BACKSPALL_NO && armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ) {
            VHTRU = (1 - (VDFSTD + VDFBND) / 2) * VLTRU; // AVERAGE NBL-TO-HBL DIFFERENCE FOR DEFORMABLE PROJ & MEDIUM-THICKNESS FACE LAYERS
            VHND = (1 - (VDFSTD + VDFBND) / 2) * VLND;
            VDFUSEDPR = (VDFSTD + VDFBND) / 2
        } else {
            VHTRU = (1 - VDFBND) * VLTRU; // AVERAGE NBL-TO-HBL DIFFERENCE FOR DEFORMABLE PROJ
            VHND = (1 - VDFBND) * VLND;
            VDFUSEDPR = VDFBND ;
        }
        } else {
        //AVERAGE NBL-TO-HBL DIFFERENCE FOR WEAK-BODIED PROJECTILES (EXCEPT BRITISH-CPC-TYPE) WHEN NO COMPLETE PEN
        VHTRU = (1 - VDFBRK) * VLTRU;
        VHND = (1 - VDFBRK) * VLND;
        //SHATR HBL IS RAISED (REDUCED PEN) FOR THESE PROJ, BUT NOT SHATR NBL

        VHSHAT = (int)((1 - VDFBRK) * VLSHAT); // DITTO FOR SHATR HBL
        VHSHATMAX = (int)((1 - VDFBRK) * VLSHATMAX);  // DITTO FOR SHATR HBL
        VDFUSEDPR = VDFBRK;
        if (VDFBRK < SHATVDF)
            SHATVDFPR = VDFBRK;
        }

        VHTRU = (int)VHTRU;
        VHND = (int)VHND;

        //'VHTRU' OR 'VHND', AS APPLICABLE, ALSO USED WHEN SHATR OCCURS W/A HARD AP CAP (Japanese UNCAPPED TYPE 91 PROJ W/CAP HEAD ATTACHED ONLY)

        //EFFECTIVE PROJ LIMIT CALC
        if (PNL == PNI && proj.criticalDamageAngle != 0)
            VITRU = -1; // MIN EFFECTIVE VEL WILL BE DETERMINED LATER BY CHANGE IN PROJ DIRECTION OF MOTION (DIFFERENT PROJ FAILURE CRITERIA)
        else  {
            // LIMIT VEL ABOVE WHICH EFFECTIVE PEN OCCUR IN MOST CASES
            // TODO: was INT
            VITRU = (int) Util.pow(armor.totalEffectiveInchesWithBacking() * MO / (PENCONST * PNI * POIMOD), VXP);
            if (VITRU < VLTRU)
                VITRU = VLTRU; // YOU CANNOT PENETRATE INTACT IF YOU CANNOT PENETRATE PERIOD!
            //IF VEL>=NBL, PROJ ALWAYS EFFECTIVE USING 'VITRU' CRITERIA IN THIS CASE.
        }

        VSCRIT = VHSHAT / (1 - (.83f * VDFSTD)); // AVE CRITICAL BRITISH CARDONALD PROJ VEL
        VSCRIT = (int)VSCRIT; //  ALWAYS USE INTEGER VALUE (TRUNCATED, NOT ROUNDED)
    } // calculateBallisticLimits()


        // DETERMINE 'PENTP' & CALC REMAINING PLUG & PROJ VELOCITIES
    void calculatePenetrationTypeAndResidualVelocities() {


        // FOR PROJ LOWER/MIDDLE BODY PIECES AT HIGH obliquity BELOW NBL OR FOR ALL SHTRD BASE PIECES,
        //   TOTAL PROJ WT ALWAYS USED IN NORMAL PLUG HOLE PUNCHING LOGIC.
        // * strikingVelocityFPS >= NBL -- COMPLETE PEN *
        // 'HF' = 1 MEANS NO UNSHTRD COMPLETE PEN (obliquity > 70 DEG)
        if (HF == 1 || strikingVelocityFPS < VLMT) {
            //* strikingVelocityFPS < NBL -- HOLING ONLY *
            if (obliquity < 45) {
                if (!SHAT || (proj.HARD == Projectile.APCAP_HARD && strikingVelocityFPS < VHSHAT && VHOL < VHSHAT)) {
                    //UNSHATRD PROJ OR Japanese UNCAPPED TYPE 91 AP W/CAP HEAD IN PLACE BELOW 'VHSHAT' (ACTS LIKE UNSHATRD PROJ)
                    if (BRK == 0)  {
                        //PROJ REMAINS IN ONE PIECE OR HAS ONLY NOSE DAMAGE
                        GOTO LTLPROJPEN;
                    } else {
                            //* PROJ ASSUMED HERE TO BE BROKEN IN MIDDLE INTO AT LEAST 2 PIECES ON TOP OF ANY NOSE DAMAGE *
                        penetrationType = PENETRATION_SEMI_UPPER_BODY; VR = -1; VDPLUG = VRSHATNS; // 'VRSHATNS' USED FOR ALL PARTIAL PEN
                        GOTO ROUNDPLUG; // EXIT
                    }
                } else
                    //SHATRD PROJ
                if (BRK == 0)
                    GOTO LTLPROJPEN; // NOSE-ONLY SHATR
                else {
                    //* PROJ ASSUMED HERE TO BE BROKEN IN MIDDLE INTO AT LEAST 2 PIECES ON TOP OF ANY NOSE DAMAGE *
                    penetrationType = PENETRATION_SEMI_UPPER_BODY; VR = -1; VDPLUG = VRSHATNS; // 'VRSHATNS' USED FOR ALL PARTIAL PEN
                    GOTO ROUNDPLUG; // EXIT
                }
                END IF
            } else {
                    //'obliquity'>=45 DEG
                    //* BASE/SIDE-FIRST HOLING W/NO DAMAGE, SHTR, OR LOWER/MIDDLE BODY DAMAGE *
                    //IF PROJ MIDDLE BODY BREAKS AT HIGH obliquity THEN ONLY LOWER BODY PIECES PEN,
                    //   BUT INCLUDES MOST OR ALL OF EXPLOSIVE FILLER.
                if (BRK > 0) {
                    //* PROJ ASSUMED HERE TO BE BROKEN IN MIDDLE INTO AT LEAST 2 PIECES ON TOP OF ANY NOSE DAMAGE *
                    penetrationType = PENETRATION_SEMI_LOWER_BODY; VDPLUG = VRSHATNS; VR = -1;
                    goto ROUNDPLUG; // EXIT
                } else {
                    //* ONLY NOSE DAMAGE OCCURS SO NOTHING PENETRATES PLATE *
                    goto LTLPROJPEN;
                }
            }


            if (obliquity >= 45)
                VDPLUG = -1; // NOTHING PENETRATES PLATE
            else
                VDPLUG = 0; // NOSE PIECES PENETRATE PLATE WITH VERY LOW REMAINING VEL


            LTLPROJPEN:
            //NONE OR FEW PROJ PIECES GO THRU PLATE

            VR = -1;
            TOTPLUGWT = NORMPLUGWT;
            DELTAPLUGWT = 0;
            if (NSBRK > 0) {
                // ONLY NOSE (33% OF PROJ BODY WEIGHT OR LESS) PENETRATES AT 'obliquity'<45 DEG
                penetrationType = PENETRATION_HOLE_REJECTED_DAMAGED_BODY;
                if (obliquity >= 45)
                    VDPLUG = -1; // NOTHING PENETRATES PLATE
            } else {
                penetrationType = PENETRATION_HOLE_REJECTED_INTACT_BODY; // INTACT PROJ REJECTED/RICOCHETS
                VDPLUG = -1; // NOTHING PENETRATES PLATE
            }

        } else {
            //* COMPLETE PEN W/DELTA PLUG *
            //DELTA PLUG, IF ANY, RIDES ON PROJ NOSE AT SAME VEL IN SAME DIRECTION.
            //PROJ BASE VEL = 'VR' (SHTRD OR NOT) & SHTRD NOSE PIECES VEL IS 'VDPLUG'
            //'VDPLUG' = 'VR' ONLY IF NO SHATR
            penetrationType = PENETRATION_COMPLETE;
            if (SHAT) {
                    //* SHATRD PROJ *
                    //BASE (OR NOSE PIECES AT obliquity >= 45 DEG):
                    //ONLY PEN IF 'strikingVelocityFPS' >= 'VLSHAT' & SUFFER EXTRA ENERGY LOSS
                    //THEY DO NOT AFFECT DELTA PLUG
                VR = VRPR;
                    //NOSE PIECES (OR BASE PORTION ONLY AT obliquity >= 45 DEG THOUGH DELTAPLUGWT = 0 HERE):
                VDPLUG = VRSHATNS;
                if (VDPLUG < VR)
                    VDPLUG = VR;

            } else {
                //* UNSHATRD PROJ *
                //PROJ REMAINING VEL = DELTA PLUG VEL (ONE VEL FOR ALL PROJ PIECES)
                if (obliquity < 45 || (obliquity >= 45 && BDYDM == 0)) {
                    VR = VRPR;
                    VDPLUG = VR;
                } else {
                    //NOSE PIECES AT obliquity >= 45 DEG W/BODY BREAKAGE:
                    VR = VRPR;
                    //BASE PIECES AT obliquity >= 45 DEG W/BODY BREAKAGE:
                    VDPLUG = VRSHATNS;
                    if (VDPLUG < VR)
                        VDPLUG = VR;
                }
            }
        }

        ROUNDPLUG:

            // USE WHOLE NUMBERS IN COMPARES
        VNPLUG = (int)VNPLUG;
        VDPLUG = (int)VDPLUG;
        VR = (int)VR;

    } // calculatePenetrationTypeAndResidualVelocities



    // CALCULATE WEIGHTS OF EJECTED ARMOR PLUGS
    // IRON WEIGHS ABOUT 0.283 LB/CUBIC INCH.  FH PLATES ALWAYS FAIL BY PUNCHING A CYLINDRICAL, CONICAL, AND/OR
    // ELLIPTICAL PLUG OF ARMOR OUT OF PLATE'S BACK.
    void calculateWeightOfEjectedPlug() {

        //   'plugMultiplier' ENLARGES HOLE IF GREATER THAN 1.
        // SHATR MULTIPLIER OVERRIDES CARTWHEEL MULTIPLIER, IF BOTH APPLY
        float plugMultiplier = 1f;


        if (SHAT)
            plugMultiplier = 1.5f; // SHATTERED VALUE WITH IRREGULAR-SHAPED, ENLARGED HOLE
        else if (CART == Armor.CARTWHEEL_BACKSPALL_EASILY)
            plugMultiplier = 2; // EXTREMELY BRITTLE PLATE GIVES WORST CASE CARTWHEEL VALUE FOR ALL IMPACTS (PROJ SHATR OVERRIDES THIS)
        else if (CART == Armor.CARTWHEEL_BACKSPALL_RESISTANCE) // TODO: CHECK THE SCOPING HERE vs OKUN
            plugMultiplier = 1f / Util.cos(obliquity);

        // BRITTLE PLATE HAS PROBLEMS AT HIGH OBLIQUITY, BUT NOT MANY AT LOW OBLIQUITY
        if (plugMultiplier > 2)
            plugMultiplier = 2;

        // DEFAULT NORMAL PLUG IS ONE-CALIBER-DIAMETER CYLINDER FOR FIRST 67% OF PLATE THICKNESS THEN EXPANDS AS A 90-DEG CONE TO PLATE BACK
        float RNDPLUGWT = .011f * armor.inchesOfArmorPlating *
                (armor.inchesOfArmorPlating * armor.inchesOfArmorPlating +
                  4.5f * proj.diameterInInches * armor.inchesOfArmorPlating +
                  20.25f * proj.diameterInInches * proj.diameterInInches
                ) * plugMultiplier;

        // NORMAL PLUG IS CYLINDER/CONE PART OF PLUG PUNCHED OUT AT RIGHT ANGLES TO FACE.
        // HOWEVER, WHEN obliquity > 45 DEG IT BEGINS TO ELONGATE
        NORMPLUGWT = RNDPLUGWT;
        if (obliquity >= 45)
            NORMPLUGWT = NORMPLUGWT / Util.cos(2 * (obliquity - 45f)) ;

        // DELTA PLUG IS ADDITIONAL PLATE MATERIAL SCOOPED WHEN PROJ NOSE COMPLETELY
        // PENETRATES & 'exitAngle' > 0 DEG, ELONGATING THE HOLE.  DOES NOT EXIST OTHERWISE.
        DELTAPLUGWT = RNDPLUGWT * ((1f / Util.cos(exitAngle)) - 1f);

        // ONLY COMPLETE NOSE PEN CREATES A DELTA PLUG
        if (strikingVelocityFPS < VLMT && (!SHAT || obliquity >= 45))
            DELTAPLUGWT = 0;

        if (effectiveThicknessInProjectileDiameters() < armor.THIN() && strikingVelocityFPS >= VLSHAT && VLTRU > VLSHAT) {
            // BASE SLAP GREATLY ELONGATES HOLE IN THIN PLATE IF VEL >= 'VLSHAT',
            // WHEN 'VLSHAT' < 'VLTRU' (= HIGH obliquity)
            NPWTPR = NORMPLUGWT / Util.cos(obliquity);

            if (effectiveThicknessInProjectileDiameters() < armor.THIN() && effectiveThicknessInProjectileDiameters() > armor.TRUTHIN())
                NPWTPR = (NPWTPR + NORMPLUGWT) / 2f;    // SPLIT THE DIFFERENCE IN WEIGHT OF PLUG THROWN

            // AVG PROJ BODY LENGTH IS ABOUT 2 CALIBERS (IGNORING TAPERED NOSE PORTION) & THIS GIVES MIN INCREASE
            // IN HOLE LENGTH USED BELOW
            if (NPWTPR < (2 * NORMPLUGWT))
                NORMPLUGWT += 2f;
            else
                NORMPLUGWT = NPWTPR;

        }
        TOTPLUGWT = NORMPLUGWT + DELTAPLUGWT; // TOTAL PLUG IS ALL MATERIAL THROWN FROM PLATE BY IMPACT

    }  //  calculateWeightOfEjectedPlug




    void setObliquityMultipliers() {

            //* PROJ obliquity MULTIPLIER FOR BOTH SHATRD & UNSHATRD PROJ FROM TABLE INTERPOLATION OR CALCULATION FORMULAE
            //'INT1' IS M/MS-TABLE INDEX & 'INT2' IS FRACTION OF 5-DEG STEP THAT obliquity IS ABOVE 'M/MS[INT1]'
        int INT1 = (int)(obliquity / 5);
        int INT2 = (int)(obliquity - 5 * INT1) / 5;

        //FIRST, DO UNSHATRD PROJ MULTIPLIER
        if (obliquity < 70) {
            //'MO' IS FOR ALL UNSHATRD PROJ, EXCEPT HBL WHEN 'VHSHAT' < 'VHTRU'
            //3-POINT FORWARD-LOOKING INTERPOLATION FORMULA
            float POINT5 = (INT1 > 11) ? 0 : .5f; // LINEAR INTERPOLATION IF obliquity > 60 DEG (POOR DATA)
            MO = M[INT1] + INT2 * (M[INT1 + 1] - M[INT1]) + POINT5 * INT2 * (INT2 - 1) * (M[INT1 + 2] - 2 * M[INT1 + 1] + M[INT1])
        } else if (obliquity == 70) {
            //obliquity = 70 DEG IS MAX FOR UNSHATRD COMPLETE PEN
            MO = 8; // MAX USABLE 'MO'
        } else
            MO = 100; // ENSURE NO PEN OCCURS AT obliquity > 70 DEG IF NO SHATR (70.01-80 DEG)

        //'MSHAT' IS FOR SHATRD PROJ (TWO VALUES DEPENDING ON 'THIN' PLATE THICKNESS THRESHOLD)
        //
        //THICK PLATE, USE FORMULAE FOR MSHAT

        //COMPUTE MSHATTHK UP TO 80 DEG FOR THIN PLATE CALC
        final float MSHATTHK = 1f / (Util.cos(1.061f * obliquity)); // THICK-PLATE 'MSHAT' USES SLIGHTLY MODIFIED SECANT CURVE FORMULA
        //
        //THIN PLATE, USE SPECIAL SHATR obliquity MULT TABLE (PLATE SHATRS, TOO)

        float MSHATTHIN;

        if (obliquity >= 80)
            MSHATTHIN = 1.51f; // MAX THIN-PLATE SHATRD obliquity = 80 DEG (VALUE FOR INT1 = 17 IS FOR 85 DEG (DUMMY))
        else {
            //3-POINT FORWARD-LOOKING INTERPOLATION FORMULA
            MSHATTHIN = MS[INT1] + INT2 *
                        (MS[INT1 + 1] - MS[INT1]) + .5f * INT2 * (INT2 - 1) * (MS[INT1 + 2] - 2f * MS[INT1 + 1] + MS[INT1]);
        }


        if (effectiveThicknessInProjectileDiameters() < armor.THIN()) {
                //ALLOW PEN UP TO 80 DEG IF NOT FULLY THICK PLATE
            if (effectiveThicknessInProjectileDiameters() < armor.THIN() && effectiveThicknessInProjectileDiameters() > armor.TRUTHIN()) {
                    //STEP DOWN TO THE THIN VALUE IN TWO INTERMEDIATE STEPS FOR ALL 'obliquity' UP TO 80 DEG.
                if (effectiveThicknessInProjectileDiameters() > (armor.TRUTHIN() + .05f))
                    MSHAT = MSHATTHIN + .625f * Util.abs(MSHATTHK - MSHATTHIN); // UPPER MIDDLE STEP
                else
                    MSHAT = MSHATTHIN + .3f * Util.abs(MSHATTHK - MSHATTHIN); // LOWER MIDDLE STEP

            } else
                MSHAT = MSHATTHIN; // MIN MSHAT BELOW 'TRUTHIN'

        } else {

            //THICK PLATE strikingVelocityFPS SHATRD PROJ MAXIMUM IS AT 75 DEG
            if (obliquity > 75)
                MSHAT = 100; // KILL PEN IF >75 DEG FOR THICK PLATE
            else if (obliquity == 75)
                MSHAT = 5.5264f; // MAX THICK-PLATE SHATRD obliquity = 75 DEG
            else
                MSHAT = MSHATTHK;

        }

    } // setObliquityMultipliers




    // DO 'CRTGD' PASS-CRTAPR-CHECK LOGIC
    void DOCRTGOOD() {

        if (NSBRK > 0 || penetrationFlag == PENFLAG_COMPLETE) {

            // can we skip test and assume lower body ok?

            boolean skipVMod = CRTAPR <= 0 ||
                    ((obliquity - exitAngle) >= CRTAPR) ||
                    (SHAT && proj.HARD != 2);


            // if not.. do the test
            if (!skipVMod) {

                CRTGD = true; // PERFORM TEST & PASSED 'criticalDamageAngle'/'noseDamageAngle' COMBINED TEST

                // MUST REVISE REMAINING VELOCITY CALC IF 'criticalDamageAngle'/'noseDamageAngle' COMBINED TEST PASSED
                VHDAM = VHND;
                VLDAM = VLND;
                if (VHDAM > VHSHATMAX)
                    VHDAM = VHSHATMAX;

                if (SHAT) {
                    // Japanese UNCAPPED TYPE 91 AP W/CAP HEAD IN PLACE
                    VLDAM = VLSHATMAX;
                }
                if (H1_Str.equals("-!#-")) {
                    H4_Str = "-#-";
                    H1_Str = "-!-"; // 'VHND' REPLACES 'VHOL' FOR POST-IMPACT CALC
                } else if (H2_Str.equals("-!#-")) {
                    H3_Str = "-#-";
                    H2_Str = "-!-"; // 'VHSHATMAX' REPLACES 'VHOL' FOR POST-IMPACT CALC
                }
                if (N1_Str.equals("-!#-")) {
                    N4_Str = "-#-";
                    N1_Str = "-!-"; // 'VLND' REPLACES 'VLMT' FOR POST-IMPACT CALC
                } else if (N2_Str.equals("-!#-")) {
                    N3_Str = "-#-";
                    N2_Str = "-!-"; // 'VLSHATMAX' REPLACES 'VLMT' FOR POST-IMPACT CALC
                }
            }
        } else
            CRTGD = true;


        if (penetrationFlag != PENFLAG_NO_LARGE_HOLE && !CRTGD)
            BRK = 4;
    }



        // SHATRD PROJ DAMAGE LOGIC (REGULAR strikingVelocityFPS NOSE-ONLY SHATR)


    void SHATRDAM() {
        //SHATRD PROJ LOGIC. SEPARATE NOSE-ONLY SHATR FROM REGULAR COMPLETE-BODY SHATR

        // ONLY NOSE BREAKAGE IF CURVED-PLATE RULE OR SOFTSHAT PLATE RULE OR UNDER MIN NOSE SHTR VEL FOR STRONGER PROJ
        if ((useCurveRule && proj.SHATRES == Projectile.SHATRES_HIGH) || (penetrationFlag > PENFLAG_NO_LARGE_HOLE && NSSHAT > 0 && NSSHAT != 2) || NSSHAT == 2) {
            NSBRK = 2;
        } else if (penetrationFlag == PENFLAG_NO_LARGE_HOLE && NSSHAT > 0) {
            NSBRK = 2; BRK = 2; BDYDM = 2; // NOSE SHATR WEAKENS PROJ SO THAT IT BREAKS UP IF VEL < HBL
        } else {
            NSBRK = 2; BRK = 1; BDYDM = 2; // REGULAR COMPLETE BODY SHATR OCCURS
        }

    } // SHATRDAM


    // CALCULATE ALL PROJECTILE PENETRATION QUALITY FACTOR MODIFIERS
    void calculateProjectileQualityModifiers() {
        float LCDAM = 0f;
        float TCAL = armor.effectiveShellShatteringInchesThick() / proj.diameterInInches;

            // LIGHTCASE BASE-FUZED PROJ LOSE PEN AT NORMAL WHEN EFFECTIVE DAMAGE-CAUSING
            // PLATE THICKNESS > 0.67 CALIBER (BRITISH CPC STANDARD). AFFECTS ALL LIMITS.
        if (proj.LTCASE == Projectile.LTCASE_MEDIUMCAVITY && TCAL > .67)
            LCDAM = .5f;
        float OPRIMEL, OPRIMED;

        OPRIMEL =  OPRIMED = obliquity; // SEPARATE THESE FOR ANY MODIFICATION LOGIC

            // LITTLE DATA AT obliquity > 60 DEG EXIST FOR PROJ USING THIS FORMULA, SO RESTRICT
            // EFFECTS TO obliquity = 45 DEG AS WORST CASE
        if (obliquity > 60)
            OPRIMEL = OPRIMED = 60;

        // BENDING/COMPRESSION/BREAKAGE DAMAGE EFFECTS ON HBL & NBL, IF USED FOR THIS PROJ
        LCMOD = LCDAM * (TCAL - .67f); // LIGHTCASE PROJ ONLY EFFECTS AT NORMAL obliquity
        POLMOD = 1 + proj.CLD * OPRIMEL - proj.ALD * TCAL * (Util.pow(OPRIMEL, proj.BLD)) - LCMOD;
        if (POLMOD > 1f)
            POLMOD = 1f;
        if (POLMOD < .1f)
            POLMOD = .1f; // MIN MOD ALLOWED (to prevent program crash in BASIC?)

        //'AED' = -1 MEANS 'POIMOD' FORMULA NOT USED BY THIS PROJ TYPE
        if (proj.AED < 0)
            POIMOD = POLMOD; // NO-OP 'POIMOD' IF NOT USED FOR THIS PROJ TYPE
        else {
            //BENDING/COMPRESSION/BREAKAGE DAMAGE EFFECTS ON EFFECTIVE LIMIT, IF USED FOR THIS PROJ TYPE
            POIMOD = 1 + proj.CED * OPRIMED - proj.AED * TCAL * (Util.pow(OPRIMED, proj.BED)) - LCMOD;
            if (POIMOD > 1)
                POIMOD = 1;
            else
                if (POIMOD < .095f)
                    POIMOD = .095f; // MIN MOD ALLOWED (to prevent a crash in BASIC?)
        }

    } // calculateProjectileQualityModifiers


    // COMPUTE MINIMUM EFFECTIVE-FILLER OR NO-NOSE-DAMAGE VELOCITY THRESHOLD VALUE
    //
    //ALWAYS USE DAMAGE/NO-DAMAGE BOUNDARY FOR 'EBL' & USE ACTUAL 'VLEX/VHEX' VALUES FOR NOSE DAMAGE CALC
    //
    //'THVAL' IS PROJ CRITICAL DEFLECTION ANGLE CALCULATED PREVIOUSLY (NOSE OR BODY, AS SELECTED)
    //'THVAL' = DEFLECTION = 'obliquity' - 'exitAngle' WHEN STRIKING VEL IS AT MIN FOR DAMAGE
    //WE MUST FIND THE STRIKING VEL THAT GIVES AN 'exitAngle' FOR GIVEN 'obliquity' SUCH THAT 'THVAL' IS EXACTLY REACHED
    //SHATRD PROJ NEVER USES THIS LOGIC ('VHEXREV' = 'THSPD' FOR NOSE-ONLY SHATTR ONLY) (HOWEVER, SHATR IS INCLUDED IN LOGIC)
    float calculateThresholdVelocity(float THVAL, float VLEXREV, float VHEXREV) {

        float THSPD = 0; // INITIALIZE FINAL CRITICAL THRESHOLD VELOCITY 'THSPD'
        if (THVAL > obliquity)
            return 0;

        //PROJECTILE DAMAGE POSSIBLE, SO DO CALCULATION OF THRESHOLD VELOCITY
        // 'EXTH' = VALUE OF 'exitAngle' WHEN VEL = CRITICAL VEL (MIN 'exitAngle' = 15
        // IF 'obliquity' >= 15 & NO SHAT)
        float EXTH = obliquity - THVAL;
        if (SHAT || (EXTH > 15 && obliquity > 15)) {
            //IF 'EXTH' > 15, THEN MINIMUM UNDAMAGED PROJ VEL IS ABOVE 'VLEXREV'
            //
            //USE ALL OF 'THVAL' FOR COMPUTATION
            //COMPUTE 'THSPD' USING INVERSE OF 'exitAngle' VEL RATIO FORMULA IF > 'VLEXREV'
            //SNCSMAX' IS DEFLECTION-AT-'VLEXREV' ('MAXDF') FUNCTION FOR (obliquity - exitAngleMIN) <= 45 DEG (SEE 'exitAngle' CALC LOGIC)
            //DIVIDE BY '(1 + OB45)' FIX IS FOR (obliquity - exitAngleMIN) > 45 DEG (CHANGE IN 'OB45' SAME AS CHANGE AT 45 DEG)

            float TEMP1 = THVAL / (1f + OB45);
            float TEMP2 = Util.sin(TEMP1) * Util.cos(TEMP1); // CRITICAL BODY OR NOSE DEFLECTION FUNCTION (AS SELECTED)
            float TEMP3 = SNCSMAX / TEMP2;
            float TEMP4 = 2 * TEMP3 - 1f;
            if (TEMP4 < 0)
                TEMP4 = 0;
            THSPD = VLEXREV * TEMP3 / Util.sqr(TEMP4);
            if (THSPD < VLEXREV)
                THSPD = VLEXREV; // VEL THAT CAUSES DEFLECTION ANGLE 'THVAL'
        } else {
            //LOGIC FOR THRESHOLD VEL 'THSPD' IF IT IS AT OR UNDER 'VLEXREV' ('EXTH' <= 15)
            //   OR IF 'obliquity' <= 15:  BOTH USE A LINEAR INCREASE IN 'exitAngle' WITH VEL BETWEEN 'VHOL' & 'VLMT'
            float THVUSD;
            if (obliquity <= 15)
                THVUSD = THVAL; // ALL OF 'THVAL' USED IN LINEAR CALC ('MAXDIFF' = 'obliquity' & MIN 'exitAngle' = 0)
            else {
                // FIND VEL BY SUBTRACTING RATIO OF DIFF BETWEEN 'exitAngle' AT 'VLEXREV'
                // ( = 'MAXDIFF' = 15) & 'exitAngle' AT MIN CRITICAL VEL
                THVUSD = 15 - EXTH; // USE ONLY PORTION OF 'THVAL' IN THE UNDER 15 REGION FOR CRITICAL VEL CALC
            }

            // LINEAR INCREASE IN 'exitAngle' WITH VEL FROM HBL TO NBL
            THSPD = VLEXREV - (THVUSD / MAXDIFF) * (VLEXREV - VHEXREV);
        }

        //ROUND 'THSPD' TO EXACTLY EQUAL 'VLMT' OR 'VHOL' IF SO ROUNDED ON DISPLAY
        if (Util.abs(THSPD - VLMT) < .5f)
            THSPD = VLMT;
        if (Util.abs(THSPD - VHOL) < .5f)
            THSPD = VHOL;


        return THSPD;

    } // calculateThresholdVelocity


    public void prepForAnotherTest() {

        // move these to firing impact refurbish
        penetrationType = PENETRATION_NONE;
        penetrationFlag = PENFLAG_NO_LARGE_HOLE;
        SHAT = false;
        NSSHAT = 0;
        BRK = 0;
        NSBRK = 0;

    }

    public void run() {
        // TODO: these were in FH55.. where did I move them?  refurbish?
        // HARD = APCAP; CAPHDRMV = 0: NOCAP = 0: WT = WTSAVE: // INIT DEFAULT VALUES FOR POSSIBLE CHANGE

        // this boolean helps me reconstruct the goto-based
        // logic of the original
        boolean gotoCAPGONE = false;


        // ONLY POST-WWI Japanese "DIVING" TYPE 88 OR 91 AP PROJ HAD A REMOVABLE NOSE-TIP
        //   CALLED A "CAP HEAD" (FLAT END UNDER IT).  LOSS OF WINDSCREEN ALWAYS CAUSED
        //   LOSS OF "CAP HEAD", WHICH WAS HELD ON ONLY BY NOTCHED WINDSCREEN THREADS.
        //   "CAP HEAD" WAS AP CAP TIP IN LARGER, CAPPED Japanese TYPE 88/91 PROJ, SO
        //   LOSING CAP HEAD KEPT MOST OF AP CAP.  UNCAPPED Japanese TYPE 91 AP W/O
        //   "CAP HEAD" & WINDSCREEN REVERTS TO PRE-WWI UNCAPPED AP & COMMON PROJ (DEFAULT #3)
        if (proj.totalWeightInPounds == proj.bodyWeightInPounds) {
            // DISCARD ALL NOSE COVERINGS
            proj.HARD = Projectile.APCAP_NONE; //SET NO AP CAP FLAG
            //IF CAPPED PROJ, USE AP-CAP-REMOVED BONUS, IF APPLICABLE
            gotoCAPGONE = true; //SKIP REST OF NOSE-COVERINGS-LOST LOGIC
        }

        //"HOOD" IS THIN SOFT-AP-CAP-LIKE NOSE COVERING FOR SCREWING ON WINDSCREEN.  IT ACTS AS A LOW-GRADE SOFT AP CAP.
        //A HOOD WILL ACT AS AN SOFT AP CAP, BUT IF IMPACT IS BELOW THE NBL, THE PROJECTILE NOSE BREAKS UP NO MATTER WHAT.
        //HOMOGENEOUS IRON PLATES > .0805 CAL THICK & ALL FH PLATES REMOVE AP CAPS OR HOODS.
        // ALL METAL PLATES REMOVE WINDSCREENS (ALL IMPACTS REMOVE GERMAN WWII ALUMINUM WINDSCREENS).
        if (!gotoCAPGONE && proj.APCapType > Projectile.APCAP_NONE &&
                proj.capHeadType != Projectile.CAP_TYPE_91_UNCAPPED &&
                proj.totalWeightInPounds > proj.bodyWeightInPounds) {

            if (BASIC.askYesNo("Has the AP Cap (and all other nose coverings) been removed?")) {
                proj.HARD = Projectile.APCAP_NONE;
                proj.totalWeightInPounds = proj.bodyWeightInPounds;
                gotoCAPGONE = true; //was goto CAPGONE; //IF YES, SKIP REST OF NOSE-COVERINGS-LOST LOGIC
            }

        }

        if (!gotoCAPGONE) {
            if (proj.capHeadType != Projectile.CAP_NONE) {
                // Japanese TYPE 88/91 AP PROJ LOGIC
                BASIC.PRINTLN("IF WINDSCREEN IS REMOVED, JAPANESE TYPE 88/91 AP/APC CAP HEAD IS ALSO REMOVED.");

                if (BASIC.askYesNo("Have the Windscreen and Cap Head been removed?")) {

                    proj.capHeadHasBeenRemoved = true;
                    if (proj.capHeadType == Projectile.CAP_TYPE_91_UNCAPPED) {
                        // UNCAPPED TYPE 91 AP PROJ (LOSES MOST OF NOSE; MUST ALSO CHANGE PROJ PEN/DAM PARAMETERS)
                        BASIC.PRINTLN("MOST OF PROJECTILE UPPER NOSE REMOVED. PROJECTILE QUALITY SEVERELY REDUCED.");
                        proj.totalWeightInPounds = proj.bodyWeightInPounds; // ONLY FLAT-NOSED BODY REMAINS
                    } else if (proj.capHeadType == Projectile.CAP_TYPE_88_91_CAPPED) {
                        // CAPPED TYPE 88/91 AP PROJ (ONLY LOSES WINDSCREEN & AP CAP TIP)
                        float WTDIFF = proj.totalWeightInPounds - proj.bodyWeightInPounds;
                        if (WCHWT >= WTDIFF)
                            WCHWT = 0; //SET WCHWT = 0 IF TOO BIG

                        BASIC.PRINTLN("MOST OF AP CAP REMAINS ATTACHED.  SOME LOSS OF PROJECTILE QUALITY.");
                        BASIC.PRINTLN("CURRENT COMBINED WINDSCREEN & CAP HEAD WEIGHT = " + WCHWT + " pound" +
                                (WCHWT > 1 ? "s" : ""));

                        int defalt = (int) ((WCHWT >= 0 && WCHWT < WTDIFF) ? WCHWT : -1);

                        WCHWT = inputBoundedInt("Combined Windscreen and Cap Head Weights (WCH), pounds", 1, 1000, defalt);


                        proj.totalWeightInPounds -= WCHWT;
                    }
                }
                // USE WHAT'S LEFT AS "totalWeightInPounds" FROM NOW ON (WITH OR WITHOUT CAP HEAD & WINDSCREEN)
            } else {

                // LOGIC FOR ALL Projectiles where capHeadType == CAP_NONE
                if (proj.APCapType <= Projectile.APCAP_NONE && proj.totalWeightInPounds > proj.bodyWeightInPounds) {
                    if (BASIC.askYesNo("Have both Hood, if any, and Windscreen been removed?")) {
                        proj.totalWeightInPounds = proj.bodyWeightInPounds;
                        proj.HARD = Projectile.APCAP_NONE;
                    }
                }

                // WINDSCREEN LOSS LOGIC
                if (proj.totalWeightInPounds > proj.bodyWeightInPounds) {

                    if (BASIC.askYesNo("Has only the Windscreen, if any, been removed?")) {
                        String s;
                        float diff = proj.totalWeightInPounds - proj.bodyWeightInPounds;
                        if (WWT > diff || (proj.APCapType != Projectile.APCAP_NONE && WWT == diff))
                            WWT = 0; // SET WWT = 0 IF TOO BIG

                        WWT = Util.inputBoundedInt("Windscreen Weight (WW), pounds", 0, 1000, (int) WWT);

                        proj.totalWeightInPounds -= WWT; //USE WHAT'S LEFT AS "proj.totalWeightInPounds" FROM NOW ON
                    }
                }
            }
        }


        // LOSS OF AP CAP ASSUMES LOSS OF ALL PROJ NOSE COVERINGS
        proj.onAPCapRemoved();

        // END OF NOSE-COVERINGS-LOST LOGIC


        float THINPRT = Util.round(armor.TRUTHIN() * proj.diameterInInches, .01f);
        float THKPRT = Util.round(armor.THIN() * proj.diameterInInches, .01f);

        BASIC.PRINTLN();

        if (armor.THKTHN == Armor.THKTHN_DUCTILE)
            BASIC.PRINTLN("THIS ARMOR IS THE MOST DUCTILE ('THKTHN' = 1) TYPE FACE-HARDENED PLATE.");
        else if (armor.THKTHN == Armor.THKTHN_FOOBAR)
            BASIC.PRINTLN("THIS ARMOR IS A MORE DUCTILE ('THKTHN' = 2) TYPE FACE-HARDENED PLATE.");
        else
            BASIC.PRINTLN("THIS ARMOR IS AN OLDER, BRITTLE ('THKTHN' = 0) TYPE FACE-HARDENED PLATE.");

        BASIC.PRINTLN("  IT IS 'THIN' BELOW " + armor.TRUTHIN() + " CALIBER = " +
                Util.inchesString(THINPRT, true) + " FOR D =" + Util.inchesString(proj.diameterInInches, true));


        // this is odd... these are never equal!
        if (armor.TRUTHIN() != armor.THIN()) {
            BASIC.PRINTLN();
            BASIC.PRINTLN("    AND IT IS 'THICK' ABOVE " + armor.THIN() + " CALIBER = " + Util.inchesString(THKPRT, true) +
                    ".  BETWEEN THESE 2 EXTREME VALUES, PROGRAM STEPS DOWN PLATE RESISTANCE TWICE.");
        } else {
            BASIC.PRINTLN(".");
            BASIC.PRINTLN("  WITH THIS ARMOR, THE CHANGE FROM 'THICK' TO 'THIN' IS ABRUPT.");
        }


        BASIC.PRINTLN();

        // CAP HEAD LOSS LOGIC FOR CAPPED Japanese TYPE 88/91 PROJ
        if (proj.capHeadType == Projectile.CAP_TYPE_88_91_CAPPED && proj.capHeadHasBeenRemoved) {
            //CAP HEAD LOSS IN CAPPED TYPE 88/91 DEGRADES PEN, BUT HAS LESS EFFECT AT obliquity > 45 DEG
            CAPHDLOSS = .045f;
            if (obliquity >= 45)
                CAPHDLOSS *= Util.pow((Util.cos(2 * (obliquity - 45f))), 2);
        } else
            CAPHDLOSS = 0;


        BRAIK = proj.BRAAK; //IF 'BRAAK', PROJ BREAKS IF IT CANNOT COMPLETELY PENETRATE
        BASIC.PRINTLN();

        // ALL user INPUTS DONE EXCEPT SHATR-ZONE-OF-MIXED-RESULTS QUESTION*

        // * SHATR & NOSE-ONLY SHATR DETERMINATION LOGIC *
        //UNLIKE HOM ARMOR, SHATR VEL ASSUMED ZERO (UNCAPPED PROJ ALWAYS SHATR)
        //   FOR ALL FH ARMORS, EXCEPT FOR TYPE_COMPOUND, WHERE ONLY CHILLED CAST IRON PROJ SHATR.
        //SHATR OVERRIDES CARTWHEEL EFFECTS (EXCEPT SPLINTERS IF NO PEN), IF BOTH
        SHAT = false;
        CART = armor.CARTWL; // INIT SHATR & INTERNAL CARTWHEEL FLAGS ('SHAT' = 1 SETS 'CART' = 0)

        // to help recreate the goto-based original code
        boolean gotoIRONPROJ = false;

        // COMPOUND ARMOR LOGIC
        if (armor.isCompound) {

            if (proj.SHATRES == Projectile.SHATRES_VERYLOW) {
                //CHILLED CAST IRON (TYPE_GRUSON/PALLISER) PROJ LOGIC
                if (proj.HARD == Projectile.APCAP_NONE) {
                    //ONLY CHILLED CAST IRON PROJ SHATR ON TYPE_COMPOUND ARMOR
                    SHAT = true;
                    // ALWAYS SHATR UNCAPPED CHILLED CAST IRON PROJ AGAINST TYPE_COMPOUND ARMOR
                    CART = Armor.CARTWHEEL_BACKSPALL_NO;
                    gotoIRONPROJ = true; // // SKIP SHATR-ZONE-OF-MIXED-RESULTS LOGIC
                }
            } else {
                gotoIRONPROJ = true; // OTHER PROJ TYPES NEVER SHATTER AGAINST TYPE_COMPOUND ARMOR
            }

            // IF TYPE_COMPOUND ARMOR, DO SHATR-ZONE-OF-MIXED-RESULTS FOR SOFT-CAPPED CHILLED CAST IRON PROJ
            // (NO HARD-CAPPED DESIGNS were EVER MADE)
        }


        if (!gotoIRONPROJ) {
            //(NO AP CAP) OR ((HOOD OR SOFT AP CAP)+((OB > MAX WORKING) OR (EXTRA-TOUGH ARMOR))) OR ((WEAK BODY & SMALL HARD AP CAP & MAX SOFTSHAT PLATE)+(PLATE DAMAGE-CAUSING EFF CAL THKNS > 0.67 CAL)+(OB > 20-DEG)) MEANS SHATR
            //ALSO INCLUDE BRIT IMPROVED WWI KC ('armor.SOFTSHAT' = 2) WHEN NOT MIDVALE UNBRKBLE PROJ ('proj.CARDONALD' NOT CARDONALD_MIDVALE) AS ALTERNATE TO 'armor.SOFTSHAT' = 1
            if (proj.HARD == Projectile.APCAP_NONE ||
                    (proj.softCapOrHood() &&
                    (obliquity > 20 || ((armor.SOFTSHAT == Armor.SHATTER_WEAK_SOFTCAPPED_PROJ &&
                    proj.CARDONALD < Projectile.CARDONALD_MIDVALE) ||
                    (armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ)))) ||
                    (armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ && proj.HARD == Projectile.APCAP_SMALLHARD && armor.effectiveShellShatteringInchesThick() / proj.diameterInInches >= .67 && impact.obliquity > 20)) {
                SHAT = true;
                CART = Armor.CARTWHEEL_BACKSPALL_NO;
            }

            if (proj.softCapOrHood() &&
                    ((armor.SOFTSHAT == Armor.SHATTER_WEAK_SOFTCAPPED_PROJ && proj.CARDONALD < Projectile.CARDONALD_MIDVALE) ||
                    armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ) && obliquity <= 20) {
                NSSHAT = 1; // PUT SOFTSHAT NOSE-ONLY SHATR RULE IN FORCE
            }


            if (obliquity > 15 && obliquity <= 20 && proj.softCapOrHood()) {
                // USER SELECTS SOFT CAP FUNCTION (RAPID ROLL-OFF W/INCREASING obliquity IN REAL IMPACTS)
                BASIC.PRINTLN("INSIDE SOFT AP CAP & HOOD 15-20 DEGREE SHATTER ZONE OF MIXED RESULTS.");
                if (!BASIC.askYesNo("  Do you wish the projectile's SOFT AP cap or HOOD to work?")) {
                    SHAT = true;
                    CART = Armor.CARTWHEEL_BACKSPALL_NO;
                    if ((armor.SOFTSHAT == Armor.SHATTER_WEAK_SOFTCAPPED_PROJ && proj.CARDONALD < Projectile.CARDONALD_MIDVALE) ||
                            armor.SOFTSHAT == Armor.SHATTER_SOFTCAPPED_PROJ) {
                        NSSHAT = 0; // USE COMPLETE SHTR AGAIN
                    }
                }
            }


            if (SHAT && NSSHAT == 0) {
                float minShatVel = 1170f * Util.cos(obliquity); //  MIN VEL FOR COMPLETE SHAT FOR STRONGER SHELLS
                if (!BRAIK && strikingVelocityFPS <= minShatVel)
                    NSSHAT = 2;
            }
        }


        IRONPROJ:
        HF = (!SHAT && obliquity > 70) ? 1 : 0; // COMPLETE PEN NOT POSSIBLE IF 'HF' = 1
        BASIC.CLS();
        BASIC.PRINTLN(BASIC.SPC(30) + "USUAL RESULTS"); // PRINT SCREEN HEADER



        // DO CALCULATIONS
        calculateBallisticLimits(); // COMPUTE VARIOUS BALLISTIC LIMITS

        BLPLUSEX(); // SELECT APPLICABLE LIMIT VELOCITIES FOR THIS CASE & COMPUTE EXIT ANGLE

        calculateDamageToProjectile(); // COMPUTE PROJ CONDITION AFTER IMPACT

        calculateWeightOfEjectedPlug(); // COMPUTE EJECTED PLUG WEIGHTS

        calculateFinalResults(); // DETERMINE WHAT HAPPENED TO PLATE
    }

}
