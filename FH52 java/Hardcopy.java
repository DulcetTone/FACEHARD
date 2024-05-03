
public class Hardcopy {

    /*

    static void hardcopyDamage() {

        //PRINT TO PRINTER NOSE & BODY DAMAGE INFO

        LPRINTLN(BDYDM1_Str);
        if (!BDYDM2_Str.equals(""))
            LPRINTLN(BDYDM2_Str);
        if (!BDYDM3_Str.equals(""))
            LPRINTLN(BDYDM3_Str); // NOSE & BODY DAMAGE COVERED ALREADY
        else {
            // NOSE DAMAGE NOT COVERED YET
            if (!NSBRK1_Str.equals("")) {
                LPRINTLN(NSBRK1_Str);
                if (!NSBRK2_Str.equals("")) {
                    LPRINTLN(NSBRK2_Str);
                    LPRINTLN(NSBRK3_Str);
                }
            }
        }


    }  //  hardcopyDamage **



    // PRINT proj.diameterInInches, strikingVelocityFPS, & obliquity ON PRINTER
    static void hardcopyOutputProjectileImpact() {

        LPRINTLN("jFacehard.Projectile Diameter (Caliber)   = " + inchesString(proj.diameterInInches, false));

        LPRINTLN("jFacehard.Projectile Striking Velocity    = " + strikingVelocityFPS + " ft/sec");
        LPRINTLN("jFacehard.Projectile jFacehard.Impact Obliquity     = " + degreesString(OBPRNT, false));

    } // hardcopyOutputProjectileImpact




    // PRINT OUT SECOND PAGE OF DATA (EBL/NBL/HBL) ON PRINTER
    static void hardcopyOutputSecondPage() {


        LPRINT(SPC(34) + "PAGE 1 OF 2"); // LPRINT CHR_Str(12); ; // FORM FEED

        LPRINTLN(SPC(12) + CALC_Str);
        LPRINTLN();
        LPRINTLN(RESNOTE_Str);
        LPRINTLN();
        LPRINTLN(NBL_Str[0] + " " + VELLTRU_Str  + " " +  N1_Str);

        LPRINT NBL_Str[1]; VELLSHAT_Str; " "; N2_Str: LPRINT NBL_Str[2]
        LPRINT NBL_Str[3]; VELLSHATMAX_Str; " "; N3_Str: LPRINT NBL_Str[4]
        LPRINT NBL_Str[5]; VELLND_Str; " "; N4_Str: LPRINT NBL_Str[6]

        LPRINTLN();

        LPRINT HBL_Str[0]; VELHTRU_Str; " "; H1_Str: LPRINT HBL_Str[1]: LPRINT HBL_Str[2]
        LPRINT HBL_Str[3]; VELHSHAT_Str; " "; H2_Str: LPRINT HBL_Str[4]
        LPRINT HBL_Str[5]; VELHSHATMAX_Str; " "; H3_Str: LPRINT HBL_Str[6]
        LPRINT HBL_Str[7]; VELHND_Str; " "; H4_Str: LPRINT HBL_Str[8]

        //PRINT EFFECTIVE B.L.
        LPRINTLN();
        LPRINTLN("'Effective' Ballistic Limit =");
        if (!EFFVEL_Str.equals(""))
            LPRINTLN(EFFVEL_Str);
        if ((VHOL <= 4000) && (!PAND_Str.equals("")))  LPRINT PAND_Str: LPRINT SPC(29);
        END IF
        if (HBLTONBL_Str.equals("")) {
            LPRINT EFFPRINT1_Str; EFFPRINT2_Str;
             if (NVRFLAG == 1)
                 LPRINT("" + criticalObliquity);
             else
                 LPRINTLN();

        } else
            LPRINTLN(HBLTONBL_Str);

        LPRINTLN();
        if (!NOTE1_Str.equals("")) {
            LPRINTLN(NOTE1_Str); LPRINTLN(NOTE2_Str);
        }
        if (!NOTE3_Str.equals(""))
            LPRINTLN(NOTE3_Str);
        if (!NOTE4_Str.equals(""))
            LPRINTLN(NOTE4_Str);
        if (!NOTE5_Str.equals("") && (proj.canBend || (MINEV >= VHOL)))
            LPRINTLN(NOTE5_Str);
        LPRINTLN();  LPRINTLN();  LPRINTLN(SPC(34) + "PAGE 2 OF 2"); // LPRINT CHR_Str(12); ; // FORM FEED


    } // ** END OF hardcopyOutputSecondPage **


    static void LPUS (int PRJ) {

        LPRINT "U.S."; PSL_Str
        if (PRJ > 13) THEN
        LPRINT "  >>NOTE:  'Mk XX-Y-Z' means 'Mark (Design) XX Mod (Model/Version) Y to Z'<<"
        END IF
        SELECT CASE PRJ
        CASE 1
            LPRINT "  Average Army/Navy (A/N) uncapped Chilled Cast Iron (Palliser) AP & Common"
        CASE 2
            LPRINT "  Average capped Chilled Cast Iron Army Coast Defense (A.C.D.) APC (1900-10)"
        CASE 3
            LPRINT "  A/N uncapped Steel 'AP Shot' (0-3.9% black-powder/Ex.D filler) (1890-1910)"
        CASE 4
            LPRINT "  A/N soft-capped Steel 'AP Shot'(0-3.9% blk-powder/Ex.D filler)(1898-1910N/45A)"
        CASE 5
            LPRINT "  A/N uncapped Steel 'AP Shell'/Common(4-6% black-powder/Ex.D filler)(1890-1945)"
        CASE 6
            LPRINT "  A/N soft-capped Steel 'AP Shell'/Common(4-6% black-pdr/Ex.D filler)(1898-1945)"
        CASE 7
            LPRINT "  Base-fuzed 7/12/14-in 'Bombardment' (Light-case) (9-11% Ex.D filler) (1914-42)"
        CASE 8
            LPRINT "  Ave. Navy 1911-23 APC except Navy Midvale 8-in Mk 11 (1911) & 'Midvale 1916'"
        CASE 9
            LPRINT "  Navy Midvale 8-in Mk 11 (1911) & all Navy 'Midvale Unbreakable 1916' APC"
        CASE 10
            LPRINT "  Ave. 1921-1935 A.C.D. APC Shot (2-3% Ex. D filler) ('Midvale Army 1921')"
        CASE 11
            LPRINT "  Ave. post-1935 A.C.D. APC Shot (1.4-2% Ex. D filler) (Ave. Navy AP c.1940)"
        CASE 12
            LPRINT "  Ave. post-WWI base-fuzed Common (4-5% Ex D filler & windscreen, but no hood)"
        CASE 13
            LPRINT "  Ave. post-WWI hooded base-fuzed Special Common (3-5% Ex. D filler) (1930-45)"
        CASE 14
            LPRINT "  6-in Mk 27-1-6 hooded base-fuzed Special Common (2.1-2.4% Ex. D filler) (1933)"
        CASE 15
            LPRINT "  8-in Mk 15-1 hard-capped base-fuzed Special Common (4.4% Ex. D filler) (1930)"
        CASE 16
            LPRINT "  3-in Mk 29-1/30-1 (to 1944), 8-in Mk 19-1-3 (to 1941) & A.C.D. Mk 20-1 APC"
        CASE 17
            LPRINT "  8-in Mk 19-4-6 APC"
        CASE 18
            LPRINT "  6-in Mk 35-1-8 & 16-in Mk 8-1-5 (to 1944) APC"
        CASE 19
            LPRINT "  8-in Mk 21-1-4, 14-in Mk 16-1-6 (to 1943) & A.C.D. Mk 20-1, and"
            LPRINT "    16-in Mk 5-1-4 (to 1944) & Mk 5-6 (ex-A.C.D. Mk 12-1 w/Mk 21 BDF) APC"
        CASE 20
            LPRINT "  3-in Mk 29-2/30-2, 6-in Mk 35-9-11, 8-in Mk 21-5, 12-in Mk 18-1,"
            LPRINT "    14-in Mk 16-7-11, and 16-in Mk 5-5 & Mk 8-6-8 APC"
        END SELECT

} // LPUS



// OUTPUT ALL RESULTS TO PRINTER *
static void hardcopyPrintAll() {

    //  BEGIN 1ST PAGE (PROJ/PLATE/RESULTS) *

    hardcopyOutputArmor(); // PRINT ARMOR TYPE

    //LPRINTLN(nation.adjectiveName() + " jFacehard.Projectile # " + proj.index);
    LPRINTLN(proj.printerName);



    LPRINTLN(SPC(26) + "SPECIFIC IMPACT CONDITIONS:");
    LPRINTLN("Plate Thickness, inches: Actual =" + round(armor.inchesOfArmorPlating, .01f) +
            " & Effective ('armor.quality'+backing) = " +
            round(armor.totalEffectiveInchesWithBacking(), .01f));
    LPRINT("Backing Thickness, inches: ");
    if (armor.totalActualInchesOfBacking() > 0) {
      if (armor.inchesOfWoodBacking > 0) {
        LPRINTLN("WOOD =" + armor.inchesOfWoodBacking);
        if (armor.inchesOfCementBacking != 0 || armor.inchesOfMetalBacking != 0)
            LPRINTLN("-- ");
        else
            LPRINTLN();
      }

      if (armor.inchesOfCementBacking > 0) {
        LPRINTLN("CEMENT = " + armor.inchesOfCementBacking);
        LPRINTLN((armor.inchesOfMetalBacking != 0 ? "-- " : ""));
      }

      if (armor.inchesOfMetalBacking != 0) {
        LPRINTLN("METAL = " + armor.inchesOfMetalBacking + " " + MTLPRNT_Str);
        LPRINTLN("*TYPE: " + BCKPRNT_Str);
      }
    } else
      LPRINTLN("NONE USED.");


    // PRINT 'proj.diameterInInches', 'strikingVelocityFPS', & 'obliquity' TO PRINTER
    hardcopyOutputProjectileImpact();

    LPRINTLN(REMV_Str );

    LPRINTLN("jFacehard.Projectile Weights, pounds: Original= " + WTSVPRNT + " *jFacehard.Impact= " + WTPRNT\ + " *Body= " + WBPRNT);

    LPRINTLN(SPC(30) + " POST-IMPACT RESULTS:" +  PEN1_Str);
    LPRINTLN(PEN2_Str);

    // PRINTLN(TO PRINTER NOSE & BODY DAMAGE INFO
    hardcopyDamage();

    if (exitAngle >= 0)
      LPRINTLN("jFacehard.Projectile Exit Angle = " + EXPRNT + "degrees");
    else
      LPRINTLN("jFacehard.Projectile Exit Angle NOT DEFINED.");


    if (penetrationFlag == PENFLAG_COMPLETE)
        LPRINTLN("jFacehard.Projectile " + WBL1_Str + " " + WBL2_Str + " " +WBL3_Str+ " " + WBL4_Str);

    if (NOPLG_Str.equals(""))
      LPRINTLN("Plug Weights, pounds: Normal = " + NPWPRNT + " & Delta = " + DPWPRNT);
    else
      LPRINTLN(NOPLG_Str); // NO PLUG THROWN


    if (!FLAKE_Str.equals(""))
        LPRINTLN(FLAKE_Str);

    if (NORMPLUGWT > .05)
        LPRINTLN("Normal Plug Velocity = " + VNPLUG + " ft/sec");

    if (!TWOVEL_Str.equals(""))
        LPRINTLN(TWOVEL_Str);

    if (RVU_Str.equals("")) {
    //PARTIAL OR COMPLETE PEN OCCURRED
      if (!BSNS1_Str.equals("")) {
        LPRINTLN(REMVEL_Str + " " + BSNS1_Str + " " +  DPLG_Str);
        if (VDPLUG >= 25)
          LPRINTLN("= " + VDPLUG + "ft/sec");
        else
          LPRINTLN("ARE VERY SLOW");

      }

      if (!BSNS2_Str.equals("")) {
        LPRINT(REMVEL_Str + " " + BSNS2_Str);
        if (VR >= 25)
          LPRINTLN(" = " + VR + " ft/sec");
        else
          LPRINTLN("ARE VERY SLOW");

      }

      if (!ONEPC_Str.equals("")) {
        //UNSHATRD PROJ, BUT INCLUDING Japanese UNCAPPED TYPE 91 AP WITH 'strikingVelocityFPS'<'VHSHAT' & 'strikingVelocityFPS'>'VHOL'
        if (penetrationFlag == PENFLAG_COMPLETE) {
          LPRINT(ONEPC_Str + " " +  DPLG_Str);
          if (DPLG_Str.equals(""))
              LPRINTLN("has");
          else
              LPRINTLN("have");

          if (VTOTAL >= 25)
            LPRINTLN(" Remaining Velocity = " +  VTOTAL + "ft/sec");
          else
            LPRINTLN(" VERY SLOW Remaining Velocity");

          if (!BSNS3_Str.equals(""))
              LPRINTLN(BSNS3_Str);
        } else  {
          if (!BSNS3_Str.equals(""))
              LPRINTLN(BSNS3_Str);
          LPRINTLN(ONEPC_Str);
        }
      } else
        if (!BSNS3_Str.equals(""))
            LPRINTLN(BSNS3_Str);

    } else
      LPRINTLN(RVU_Str); // NO PARTIAL OR COMPLETE PEN


    //BEGIN 2ND PAGE (ALL BALLISTIC LIMITS)
    hardcopyOutputSecondPage();

} // hardcopyPrintAll


//* PRINT ARMOR TYPE ON PAPER
static void hardcopyOutputArmor() {

      LPRINTLN(SPC(4) + "USUAL RESULTS OF NATHAN OKUN FACE HARDENED ARMOR PENETRATION PROGRAM (c)"
        LPRINTLN(SPC(23) + "(VERSION 5.5 DATED 22 JANUARY 2004)");
        LPRINTLN("ALL FACE HARDENED ARMOR PLATE TYPES:"

        for (int i=0; i< jFacehard.Armor.NUM_ARMOR_TYPES; i++) {
            String leader = (i + 1 < 10) ? "   " : "  ";
            LPRINTLN(leader + i + ". " + jFacehard.Armor.longNames[i +1]);
        }

        //
      // PRINT ARMOR TYPE & PARAMETERS TO PRINTER

        LPRINTLN(SPC(25); ">>>>>>>> TYPE USED ="; armorPlateType; "<<<<<<<<");
        LPRINTLN("SEE PROGRAM ARMOR PARAMETER CHANGE LOGIC FOR MEANING OF VALUES & FLAGS.");

        if (armorPlateType == jFacehard.Armor.TYPE_GRUSON || armorPlateType == jFacehard.Armor.TYPE_TC) {
            LPRINTLN("TYPE_GRUSON & TC ARMORS:  Soft Back Layer Thickness 'UB' varies with thickness.");
            LPRINTLN("Calculated default 'UB' value for this plate given in 'ORIGINAL ARMOR' data.");
        }

        LPRINTLN("FLAGS = '0'(CLEAR), '1'(SET), or, for SOFTSHAT, CARTWL & THKTHN, '2'(ALTERNATE)");

        LPRINTLN("ORIGINAL ARMOR TABLE PARAMETER VALUES:" + TAB(41); "UB ="; oldArmor.UB; TAB(52); "Q="; oldArmor.quality; TAB(64); "QDAM ="; oldArmor.shellShatteringQuality
        LPRINTLN("  FLAGS: CARTWL="; oldArmor.CARTWL; TAB(21); "CMPND="; oldArmor.isCompound; TAB(31);
        LPRINTLN("THNCHL="; oldArmor.isThinlyFaced; TAB(42); "SOFTSHAT="; oldArmor.SOFTSHAT; TAB(55); "THKTHN="; oldArmor.THKTHN
        LPRINTLN("CURRENT ARMOR PARAMETER VALUES:"; TAB(41); "UB ="; armor.UB; TAB(52); "Q ="; armor.quality; TAB(64); "QDAM ="; armor.shellShatteringQuality
        LPRINTLN("  FLAGS: CARTWL="; armor.CARTWL; TAB(21); "CMPND="; armor.isCompound; TAB(31);
        LPRINTLN("THNCHL="; armor.isThinlyFaced; TAB(42); "SOFTSHAT="; armor.SOFTSHAT; TAB(55); "THKTHN="; armor.THKTHN
        NATN_Str = STR_Str(NATION): PRJ_Str = STR_Str(PROJ);

     //   PSL_Str = "  " + nation.name() + ") PROJECTILE TYPE (SELECTION =" + PRJ_Str + "):";


}
              */

}
