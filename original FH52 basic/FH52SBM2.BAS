' >>>>**** FACE-HARDENED ARMOR PENETRATION PROGRAM BY NATHAN OKUN ****<<<<
'               >>>>**** VERSION 5.2 OF 7 JUNE 2002 ****<<<<
'      >>>>**** 2ND PRINT & SUBPROGRAM MODULE "FH52SBM2.BAS" ****<<<<
'



' ** END OF MODULE-LEVEL "FH40SBM2.BAS" CODE **

SUB BACKPRINT STATIC
'
' PRINT METAL BACKING LAYER TYPES
'
PRINT : PRINT "Select general type of backing plate from the following table:": PRINT
BKNGT1$ = "Wrought Iron": BKNGQ1$ = " (Q = 0.6)"
BKNG1$ = BKNGT1$ + "                                                   " + BKNGQ1$: BKPRT1$ = BKNGT1$ + BKNGQ1$
BKNGT2$ = "Mild (Medium) Steel thru WWI": BKNGQ2$ = " (Q = 0.7)"
BKNG2$ = BKNGT2$ + "                                   " + BKNGQ2$: BKPRT2$ = BKNGT2$ + BKNGQ2$
BKNGT3$ = "High Tensile Steel thru WWI, Nickel Steel, Post-WWI Mild Steel": BKNGQ3$ = " (Q = 0.8)"
BKNG3$ = BKNGT3$ + " " + BKNGQ3$: BKPRT3$ = BKNGT3$ + BKNGQ3$
BKNGT4$ = "Post-WWI High Tensile Steel & British/Japanese Ducol (D) Steel": BKNGQ4$ = " (Q = 0.9)"
BKNG4$ = BKNGT4$ + " " + BKNGQ4$: BKPRT4$ = BKNGT4$ + BKNGQ4$
BKNGT5$ = "All Special Treatment (homogeneous Krupp-armor grade) Steels": BKNGQ5$ = " (Q = 1.0)"
BKNG5$ = BKNGT5$ + "   " + BKNGQ5$: BKPRT5$ = BKNGT5$ + BKNGQ5$
PRINT " 1. " + BKNG1$: PRINT " 2. " + BKNG2$: PRINT " 3. " + BKNG3$
PRINT " 4. " + BKNG4$: PRINT " 5. " + BKNG5$
PRINT
'
'  ** END OF SUB BACKPRINT **
'
END SUB

SUB CHGARMRPRNT STATIC
'
' PRINT A MESSAGE IF ARMOR HAS HAD ANY DEFAULT VALUES CHANGED BY USER.
'
PRINT
ARMRCHGD = 0
IF ((UBSV <> UB) OR (QSV <> Q) OR (QDAMSV <> QDAM)) THEN
   ARMRCHGD = 1
ELSEIF ((CARTWLSV <> CARTWL) OR (CMPNDSV <> CMPND)) THEN
   ARMRCHGD = 1
ELSEIF ((THNCHLSV <> THNCHL) OR (SOFTSHATSV <> SOFTSHAT)) THEN
   ARMRCHGD = 1
ELSEIF (THKTHNSV <> THKTHN) THEN
   ARMRCHGD = 1
END IF
IF (ARMRCHGD = 1) THEN PRINT "ARMOR HAS BEEN CHANGED FROM DEFAULT PARAMETERS."
'
'  ** END OF SUB CHGARMRPRNT **
END SUB

SUB DAMAGESETUP STATIC
' SET UP TO PRINT PROJECTILE DAMAGE MESSAGES
'
IF ((CRVFLAG = 1) AND ((MINEV <= 0) OR (MINEV >= VHOL))) THEN
'NOTE: IF NOT TRUE, THEN PROJ ALREADY EFFECTIVE WHEN CURVED-PLATE RULE BEGINS AT HBL (SKIP LOGIC)
'CURVED-PLATE RULE APPLIED
  IF ((MINEV = 0) OR (MINEV1 = -1) OR (MINEV3 = -1)) THEN
    MINEV = 0: NOTEFLAG = 2: 'REGULAR CURVED-PLATE RULE FORCES NOSE-ONLY SHATR
  ELSEIF ((MINEV > VHOL) AND (MINEV <= VLMT)) THEN
    NOTEFLAG = 3: 'CURVED-PLATE RULE LOWERS EFFECTIVE LIMIT TO HBL
  ELSEIF (MINEV > VLMT) THEN
    NOTEFLAG = 4: 'GAP EXISTS BETWEEN EFFECTIVE LIMIT AND CURVED-PLATE RULE VEL REGION
  END IF
END IF
'
IF (LTCASE > 0) THEN
'LARGE-CAVITY PROJ
  IF (SHAT = 1) THEN
    EFFPRINT1$ = " NEVER (NOSE SHATTER REACHES CAVITY)"
    GOTO SOFTSHATRULE: 'NOSE-ONLY SHATR DOES NOT PREVENT CAVITY DAMAGE OF A LIGHTCASE PROJ
  END IF
  IF ((MINEV2 > 0) AND (MINEV = MINEV2)) THEN
    NSFLG = 1: NOTEFLAG = 0: 'ALL NOSE BREAKAGE OF A LIGHTCASE PROJ CAUSES LOSS OF EFFECTIVENESS
  END IF
END IF
'*
'EFFECTIVE PROJ
'
IF (NOTEFLAG >= 2) THEN
'CURVED-PLATE RULE
  IF (NOTEFLAG = 4) THEN
    EFFVEL$ = VEL$
    IF (VHOL <= 4000) THEN PAND$ = " *AND*"
  END IF
  IF (((NOTEFLAG = 2) OR (NOTEFLAG = 4)) AND (VHOL <= 4000)) THEN
    HBLTONBL$ = " BETWEEN HOLING BL & NAVY BL (SEE BELOW)"
    GOTO CRVPLATERULE: 'REGULAR RULE USE
  END IF
END IF
'
IF (NOTEFLAG = 3) THEN
  MINEV = VHOL: 'MIN EFFECTIVE LIMIT LOWERED TO HBL BY CURVED-PLATE RULE
ELSE
  IF ((MINEV = 0) AND (MINEV1 <> -1) AND (MINEV3 <> -1)) THEN
    EFFPRINT1$ = " USUALLY EFFECTIVE (CAVITY IMMUNE TO NOSE DAMAGE)"
    GOTO SOFTSHATRULE
  END IF
END IF
'
IF ((MINEV = 0) AND (MINEV1 = -1)) THEN
  EFFPRINT1$ = " NEVER (COMPLETE SHATTER)"
  GOTO ENDSPG: 'EXIT
END IF
'
IF (MINEV3 = -1) THEN
  NVRFLAG = 1
  EFFPRINT1$ = " RARELY.  EXCEEDS BREAKAGE ANGLE (DEGREES):"
  GOTO ENDSPG: 'EXIT
END IF
'
IF ((HARD = -1) AND (SHATRES = 1) AND (MINEV <= VLMT)) THEN
  EFFPRINT1$ = " NAVY BL"
  NVRFLAG = 0: NSFLG = 1
ELSEIF ((MINEV <> VLMT) AND (MINEV > VHOL)) THEN
  EFFVEL$ = VEL$
ELSE
  IF (MINEV = VLMT) THEN
    EFFPRINT1$ = " NAVY BL"
  ELSE
    EFFPRINT1$ = " HOLING BL"
    IF (NOTEFLAG = 3) THEN
      EFFPRINT2$ = " (DUE TO CURVED PLATE) (SEE BELOW)"
      GOTO CRVPLATERULE
    END IF
  END IF
END IF
'
IF (NVRFLAG = 0) AND (NSFLG = 1) THEN EFFPRINT2$ = " (NOSE DAMAGE REACHES CAVITY)"
'
GOTO SOFTSHATRULE
'*
CRVPLATERULE:
' PRINT OUT CURVE PLATE RULE ON SCREEN OR ON PAPER WHEN IT APPLIES
'
NOTE1$ = "SPECIAL CURVED-PLATE RULE FOR BODY DAMAGE:"
NOTE2$ = "  Projectiles remain effective if they are steel & hit curved plates at over 45"
NOTE3$ = "  degrees obliquity with Striking Velocities between plate Holing BL & Navy BL."
IF (SHAT = 1) THEN
  NOTE4$ = "  Complete shatter of this projectile will occur otherwise."
ELSEIF (BEND = 1) AND (OB >= OBCRIT) AND (CARDONALD = 0) THEN
  NOTE4$ = "  Complete breakup of this projectile will occur otherwise."
ELSE
  IF (MINEV = VHOL) THEN
    NOTE4$ = "  Other effects keep projectile effective above Navy BL."
  END IF
END IF
'*
GOTO ENDSPG: 'EXIT
'*
SOFTSHATRULE:
IF (NOTEFLAG = 1) THEN
  NOTE1$ = "SPECIAL HOOD IF VS>=NBL OR SOFT CAP & EXTRA-TOUGH PLATE NOSE-ONLY SHATTER RULE:"
  NOTE2$ = "  Nose-only shatter usually occurs if OB<=15 deg and sometimes if 15<OB<20 deg."
  NOTE3$ = "  Complete shatter of this projectile against this armor will occur otherwise."
END IF
'*
ENDSPG:
' ** END SUB DAMAGESETUP **
END SUB

SUB DOCRTGOOD STATIC
' DO 'CRTGD' PASS-CRTAPR-CHECK LOGIC
'
IF ((NSBRK > 0) OR (PENFLG = 2)) THEN
'*
  IF ((CRTAPR <= 0) OR ((CRTAPR > 0) AND ((OB - EX) >= CRTAPR)) OR ((SHAT = 1) AND (HARD <> 2))) THEN GOTO SKIPVMOD: 'TEST FAILED
'*
  CRTGD = 1: 'PERFORMED & PASSED 'CRITAGL'/'NSDAMAGL' COMBINED TEST
'*
'MUST REVISE REMAINING VELOCITY CALC IF 'CRITAGL'/'NSDAMAGL' COMBINED TEST PASSED
  VHDAM = VHND: VLDAM = VLND
  IF (VHDAM > VHSHATMAX) THEN
    VHDAM = VHSHATMAX
  END IF
  IF (SHAT = 1) THEN
'JAP UNCAPPED TYPE 91 AP W/CAP HEAD IN PLACE
    VLDAM = VLSHATMAX
  END IF
  IF (H1$ = "-!#-") THEN
    H4$ = "-#-": H1$ = "-!-": ''VHND' REPLACES 'VHOL' FOR POST-IMPACT CALC
  ELSEIF (H2$ = "-!#-") THEN
    H3$ = "-#-": H2$ = "-!-": ''VHSHATMAX' REPLACES 'VHOL' FOR POST-IMPACT CALC
  END IF
  IF (N1$ = "-!#-") THEN
    N4$ = "-#-": N1$ = "-!-": ''VLND' REPLACES 'VLMT' FOR POST-IMPACT CALC
  ELSEIF (N2$ = "-!#-") THEN
    N3$ = "-#-": N2$ = "-!-": ''VLSHATMAX' REPLACES 'VLMT' FOR POST-IMPACT CALC
  END IF
SKIPVMOD:
ELSE
  CRTGD = 1: ''CRITAGL'/'NSDAMAGL' COMBINED TEST NOT DONE & LOWER BODY ASSUMED OK
END IF
'
IF ((PENFLG > 0) AND (CRTGD = 0)) THEN BRK = 4
'
' ** END OF SUB DOCRTGOOD **
END SUB

SUB GETPROJDWTWB STATIC
'
' PRINT OLD D, WT, WB AND ASK USER FOR NEW VALUES.  SET/CLEAR 'CAPHD' FLAG
IF (D > 0) THEN
  PRINT "CURRENT PROJECTILE DIAMETER ="; D; "inch";
  IF (D > 1!) THEN PRINT "es":  ELSE PRINT
END IF
DIAMETER:
INPUT "Projectile diameter (D), inches: ", PRJD$
IF ((PRJD$ = "") AND (D > 0)) THEN
  PRINT "Using"; D
ELSE
  D = VAL(PRJD$)
END IF
IF (D <= 0) THEN GOTO DIAMETER: 'BAD INPUT
IF (WTSAVE > 0) THEN
  PRINT "CURRENT PROJECTILE TOTAL WEIGHT ="; WTSAVE; "pound";
  IF (WTSAVE > 1!) THEN PRINT "s":  ELSE PRINT
END IF
TOTALWT:
INPUT "Projectile total weight (WT), pounds: ", WT$
IF ((WT$ = "") AND (WTSAVE > 0)) THEN
  PRINT "Using"; WTSAVE: WT = WTSAVE
ELSE
  WT = VAL(WT$): WTSAVE = WT
END IF
IF (WT <= 0) THEN GOTO TOTALWT: 'BAD INPUT
CAPHD = 0: 'RESET 'CAP HEAD' FLAG
IF ((NATION = 6) AND (PROJ >= 9)) THEN
'JAP TYPE 88/91 AP/APC PROJ W/'CAP HEAD' LOGIC: UNCAPPED(AP)=11 & CAPPED(APC)=9 & 10
  IF (PROJ = 11) THEN
    CAPHD = 1: 'UNCAPPED 15.5/20.3CM TYPE 91 AP (20CM TYPE 88 HAS AP CAP, I THINK)
    PRINT "'CAP HEAD' IS NOT PART OF BODY BECAUSE IT SHATTERS BEFORE NOSE DOES."
  ELSE
    CAPHD = 2: 'CAPPED TYPE 88/91 AP
  END IF
END IF
IF (WB > 0) THEN
  PRINT "CURRENT PROJECTILE BODY WEIGHT ="; WB; "pound";
  IF (WB > 1!) THEN PRINT "s":  ELSE PRINT
END IF
BODYWT:
INPUT "Projectile body weight (WB), pounds (Maximum=WT & Minimum=WT/2): ", WB$
IF ((WB$ = "") AND (WB >= WT / 2) AND (WB <= WT)) THEN
  PRINT "Using"; WB
ELSE
  WB = VAL(WB$)
END IF
IF ((WB < (WT / 2)) OR (WB > WT)) THEN GOTO BODYWT: 'BAD INPUT
PRINT
SOFTQPTMP = 1! - (1.1 * ((WT - WB) / WT) - .0268): 'MAXIMUM QUALITY FOR SOFT-CAPPED PROJ
SOFTQPTEMP = INT(1000 * SOFTQPTMP + .5): SOFTQPMAX = SOFTQPTEMP / 1000
IF (SOFTQPMAX > 1!) THEN SOFTQPMAX = 1!: 'QUALITY CANNOT BE IMPROVED BY A SMALL SOFT CAP
'
' ** END SUB GETPROJDWTWB **
END SUB

SUB IMPACTPRNT STATIC
'
' PRINT PROJ IMPACT INFORMATION
'
PRINT "Projectile Diameter (Caliber)   ="; D; "inches -- Nation ="; NATION; "& Type ="; PROJ
PRINT "Projectile Striking Velocity    ="; VS; "ft/sec"
OBPRNT = INT(100 * OB + .5) / 100: EXPRNT = INT(100 * EX + .5) / 100
PRINT "Angles, degrees:  Obliquity     ="; OBPRNT; "& Exit =";
IF (EX >= 0) THEN PRINT EXPRNT:  ELSE PRINT " NOT DEFINED"
'
' NOSE COVERING LOSS INFO
IF (WB = WTSAVE) THEN
  REMV$ = "Projectile does not use any nose coverings by design."
ELSEIF (WT = WTSAVE) THEN
  REMV$ = "All projectile nose coverings intact and in place on impact."
ELSEIF (WT = WB) THEN
  REMV$ = "All projectile nose coverings stripped off by prior impact."
ELSEIF (CAPHD > 0) THEN
  REMV$ = "Windscreen and Cap Head stripped off by prior impact.  AP cap intact."
ELSE
  IF (APCAP < 0) THEN CAP$ = "  Hood intact.":  ELSE CAP$ = "  AP cap intact."
  REMV$ = "Windscreen stripped off by prior impact." + CAP$
END IF
PRINT REMV$
'
WTSVPRNT = INT(100 * WTSAVE + .5) / 100: WTPRNT = INT(100 * WT + .5) / 100
WBPRNT = INT(100 * WB + .5) / 100
PRINT "Projectile Weights, pounds: Original="; WTSVPRNT; "*Impact="; WTPRNT; "*Body="; WBPRNT
'
'  ** END OF SUB IMPACTPRNT **
END SUB

SUB REMVELPRNT STATIC
'PRINT REMAINING VELOCITY MESSAGES ON SCREEN
'
IF (VDPLUG < 0) THEN
  RVU$ = "Projectile Remaining Velocity NOT DEFINED"
  PRINT RVU$: ' NO PARTIAL OR COMPLETE PEN
ELSE
'
' CALC AVE PROJ REMAINING VEL FOR 1-PIECE DAMAGED PEN, IF USED & DIFFERENT FROM 'VDPLUG'
  IF (VDPLUG <> VR) THEN
    VTMP = (VDPLUG ^ 2 + VR ^ 2) / 2
    VTEMP = SQR(VTMP): VTOTAL = INT(VTEMP + .5): 'K.E. UNCHANGED
  END IF
  IF (DELTAPLUGWT > .05) THEN DPLG$ = "& Delta Plug ": ' ADD DELTA PLUG, IF THERE
'
  IF ((SHAT = 0) OR (PENTP = 3)) THEN
'UNSHATRD PROJ & UNCAPPED JAP TYPE 91 AP W/'VS'<'VHSHAT' BUT 'VS'>'VHOL' ('PENTP'=3)
    IF (OB < 45!) THEN
      IF (PENTP = 2) THEN
        BSNS3$ = "No Part of Projectile completely penetrates plate."
      END IF
      IF (PENTP = 3) THEN
        IF ((SHAT = 1) OR ((SHAT = 0) AND (NSBRK > 0))) THEN
          BSNS1$ = "Nose Pieces "
          BSNS3$ = "Projectile Body up to forward bourrelet fails to completely penetrate."
        ELSE
          BSNS3$ = "No Part of Projectile completely penetrates plate."
        END IF
      END IF
      IF (PENTP = 5) THEN
        IF (NSBRK = 0) THEN
          BSNS1$ = "Nose & Upper Body "
        ELSE
          BSNS1$ = "Nose Pieces & Upper Body"
        END IF
        IF (BDYDM < 2) THEN
          BSNS3$ = "Projectile Lower Body fails to completely penetrate."
          ONEPC$ = "If Projectile Body not broken up, No Part of Projectile completely penetrates."
        ELSE
          BSNS3$ = "Projectile Lower Body Pieces fail to completely penetrate."
        END IF
      END IF
      IF (PENTP = 6) THEN
        IF (BDYDM = 1) THEN
          IF (VDPLUG <> VR) THEN
            IF (NSBRK = 0) THEN
              BSNS1$ = "Nose & Upper Body "
            ELSE
              BSNS1$ = "Nose Pieces & Upper Body "
            END IF
            BSNS2$ = "Lower Body "
            ONEPC$ = "If Proj not broken, it ": 'ONLY IF PROJ DEFORMED, NOT BROKEN
          ELSE
            BSNS1$ = "Entire Projectile "
          END IF
        ELSEIF (BDYDM = 2) THEN
          IF (VDPLUG <> VR) THEN
            BSNS1$ = "Nose & Upper Body Pieces "
            BSNS2$ = "Lower Body Pieces "
          ELSE
            BSNS1$ = "All Projectile Pieces "
          END IF
        ELSE
'NO PROJ DAMAGE
          BSNS1$ = "Entire Projectile "
        END IF
      END IF
    ELSE
''OB'>=45 DEG
      IF (PENTP = 2) THEN
        BSNS3$ = "No Part of Projectile completely penetrates plate."
      END IF
      IF (PENTP = 3) THEN
' ONLY NOSE DAMAGE OCCURS (ABOVE FORWARD BOURRELET)
        BSNS3$ = "Entire Projectile ricochets off of plate."
      END IF
      IF (PENTP = 4) THEN
        IF (NSBRK = 0) THEN
          BSNS1$ = "Lower Body "
          BSNS3$ = "Projectile Nose & Upper Body ricochet off of plate."
        ELSE
          BSNS1$ = "Lower Body & Some Nose Pieces "
          BSNS3$ = "Most of Projectile Nose & Upper Body Pieces ricochet off of plate."
        END IF
        ONEPC$ = "If Projectile Body not broken up, All of Projectile ricochets off of plate."
      END IF
      IF (PENTP = 6) THEN
        IF (BDYDM = 1) THEN
          IF (VDPLUG <> VR) THEN
            IF (NSBRK = 0) THEN
              BSNS2$ = "Nose & Upper Body "
            ELSE
              BSNS2$ = "Nose Pieces & Upper Body "
            END IF
            BSNS1$ = "Lower Body "
          ELSE
            BSNS1$ = "Entire Projectile "
          END IF
          ONEPC$ = "If Proj not broken, it "
        ELSEIF (BDYDM = 2) THEN
          IF (VDPLUG <> VR) THEN
            BSNS1$ = "Lower Body Pieces "
            BSNS2$ = "Nose & Upper Body Pieces "
          ELSE
            BSNS1$ = "All Pieces "
          END IF
        ELSE
'NO PROJ DAMAGE
          BSNS1$ = "Entire Projectile "
        END IF
      END IF
    END IF
  ELSE
'SHATR ('SHAT'=1) EXCEPT 'VHSHAT'<='VS'<'VLSHAT' ('PENTP'=3)
'  INCLUDING JAP UNCAPPED TYPE 91 AP W/'VS'<'VHSHAT'
    IF (OB < 45!) THEN
      BSNS1$ = "All Nose Pieces "
      IF (VR >= 0) THEN
        IF (VDPLUG <> VR) THEN
          BSNS2$ = "Body Pieces "
        ELSE
          BSNS1$ = "All Pieces "
        END IF
      ELSE
        IF (VS >= VHSHAT) THEN
          BSNS3$ = "Projectile Body does not completely penetrate."
        END IF
      END IF
    ELSE
''OB'>=45 DEG
      BSNS1$ = "Body & Some Nose Pieces "
      IF (VR >= 0) THEN
        IF (VDPLUG <> VR) THEN
          BSNS2$ = "Most Nose Pieces "
        ELSE
          BSNS1$ = "All Pieces "
        END IF
      ELSE
        BSNS3$ = "Most Projectile Nose Pieces do not completely penetrate."
      END IF
    END IF
  END IF
'*
  IF (BSNS1$ <> "") THEN
    PRINT REMVEL$; BSNS1$; DPLG$;
    IF (VDPLUG >= 25) THEN
      PRINT "="; VDPLUG; "ft/sec"
    ELSE
      PRINT "ARE VERY SLOW"
    END IF
  END IF
  IF (BSNS2$ <> "") THEN
    PRINT REMVEL$; BSNS2$;
    IF (VR >= 25) THEN
      PRINT "="; VR; "ft/sec"
    ELSE
      PRINT "ARE VERY SLOW"
    END IF
  END IF
  IF (ONEPC$ <> "") THEN
    IF (PENFLG = 2) THEN
      PRINT ONEPC$; DPLG$;
      IF (DPLG$ = "") THEN PRINT "has"; :  ELSE PRINT "have";
      IF (VTOTAL >= 25) THEN
        PRINT " Remaining Velocity ="; VTOTAL; "ft/sec"
      ELSE
        PRINT " VERY SLOW Remaining Velocity"
      END IF
      IF (BSNS3$ <> "") THEN PRINT BSNS3$
    ELSE
      IF (BSNS3$ <> "") THEN PRINT BSNS3$
      PRINT ONEPC$
    END IF
  ELSE
    IF (BSNS3$ <> "") THEN PRINT BSNS3$
  END IF
END IF
'
'** END OF SUB REMVELPRNT **

END SUB

SUB VERSIONPRNT (VCOUNT)

' PRINT DEFINITIONS AND NEW VERSION INFORMATION (5.2 HERE)

SELECT CASE VCOUNT
  CASE 1
    PRINT "IMPORTANT DEFINITIONS:"
    PRINT "NBL = MINIMUM STRIKING VELOCITY TO ALLOW PROJECTILE TO COMPLETELY PASS THROUGH": '1
    PRINT "      PLATE WITH THE MINIMUM POSSIBLE REMAINING VELOCITY.  IF PROJECTILE BREAKS": '2
    PRINT "      THEN 80% OF THE BODY MAKES IT THROUGH.  ALWAYS IGNORE AP CAP, HOOD, OR": '3
    PRINT "      WINDSCREEN, IF ANY, IN THIS DETERMINATION. (NAVY BALLISTIC LIMIT)": '4
    PRINT "HBL = MINIMUM STRIKING VELOCITY TO MAKE A HOLE IN THE PLATE OF ROUGHLY CALIBER": '5
    PRINT "      SIZE OR GREATER, PUNCHING OUT A PLUG OF ARMOR THROUGH PLATE BACK, INTACT": '6
    PRINT "      OR BROKEN UP.  MAY BE JUST BELOW OR FAR BELOW NBL, DEPENDING ON PLATE": '7
    PRINT "      TYPE, PROJECTILE TYPE, PROJECTILE CONDITION AFTER IMPACT, & OBLIQUITY.": '8
    PRINT "      A LITTLE ABOVE 'THROUGH-CRACK' BALLISTIC LIMIT. (HOLING BALLISTIC LIMIT)": '9
    PRINT "EBL = MINIMUM STRIKING VELOCITY, WHEN IT EXISTS, TO ALLOW PROJECTILE FILLER TO": '10
    PRINT "      EXPLODE PROPERLY (FUZE & BASE PLUG OK AND NO CAVITY CRACKS). NOSE DAMAGE": '11
    PRINT "      WILL ONLY COMPROMISE THIS IF PROJECTILE HAS A VERY LARGE CAVITY (OVER 6%": '12
    PRINT "      FILLER CAN FIT INTO CAVITY, EVEN IF LESS WAS USED) OR USES A NOSE FUZE.": '13
    PRINT "      USUALLY, I ASSUME PROJECTILE BODY COMPLETELY INTACT, THOUGH NOSE-ONLY": '14
    PRINT "      SHATTER, AMONG OTHER THINGS, MAY MODIFY THIS. (EFFECTIVE BALLISTIC LIMIT)": '15
    PRINT "OB  = ANGLE OF PROJECTILE CENTERLINE COMPARED TO NORMAL (RIGHT ANGLES) LINE AT": '16
    PRINT "      CENTER OF IMPACT SITE (YAW ASSUMED ZERO OR ALREADY INCLUDED). MAXIMUM OB": '17
    PRINT "      USED (DEG): NO SHATTER-->70; IF SHATTER-->75 W/THICK PLATE, 80 OTHERWISE": '18
    PRINT "EX  = ANGLE PROJECTILE REMAINING VELOCITY VECTOR MAKES WITH NORMAL LINE ABOVE": '19
    PRINT "      WHEN PROJECTILE EXITS PLATE BACK (ALSO COMPUTED FOR PLUG VELOCITY CALCS": '20
    PRINT "      EVEN IF PROJECTILE FAILS TO COMPLETELY PENETRATE, BUT IS ABOVE HBL).": '21
    PRINT "CONTINUE? (Y=Yes/N=Skip Change Information (Any other entry does nothing)) ";
  CASE 2
    PRINT "VERSION 5.2 CHANGE INFORMATION:"
    PRINT "(1)  Corrected a Remaining Velocity (VR) bug & a OB > 75 deg calculation bug."
    PRINT "     THANKS YET AGAIN, MIKE, FOR YOUR NUMEROUS COMMENTS ON THESE PROBLEMS!"
    PRINT "     THEY HELPED ME FIND THE ERRORS AND FIX THEM FAST!!!"
'    PRINT "CONTINUE? (Y=Yes/N=Skip Rest (Any other entry does nothing)) ";
    PRINT "FINISHED? Y=Yes (Any other entry does nothing) ";
  CASE 3
    PRINT "VERSION 5.X CHANGES CONTINUED:"
    PRINT "(X)  ": '1
    PRINT "     ": '2
    PRINT "     ": '3
    PRINT "     ": '4
    PRINT "     ": '5
    PRINT "     ": '6
    PRINT "     ": '7
    PRINT "     ": '8
    PRINT "     ": '9
    PRINT "     ": '10
    PRINT "     ": '11
    PRINT "     ": '12
    PRINT "     ": '13
    PRINT "     ": '14
    PRINT "     ": '15
    PRINT "     ": '16
    PRINT "     ": '17
    PRINT "     ": '18
    PRINT "     ": '19
    PRINT "     ": '20
    PRINT "     ": '21
    PRINT "CONTINUE? (Y=Yes/N=Skip Rest (Any other entry does nothing)) ";
    PRINT "FINISHED? Y=Yes (Any other entry does nothing) ";
  CASE 4
    PRINT "VERSION 5.X CHANGES CONTINUED:"
    PRINT "(X)  "
    PRINT "CONTINUE? (Y=Yes/N=Skip Rest (Any other entry does nothing)) ";
    PRINT "FINISHED? Y=Yes (Any other entry does nothing) ";
  CASE 5
    PRINT "VERSION 5.X CHANGES CONTINUED:"
    PRINT "(X)  "
    PRINT "CONTINUE? (Y=Yes/N=Skip Rest (Any other entry does nothing)) ";
    PRINT "FINISHED? Y=Yes (Any other entry does nothing) ";
'FOR FUTURE USE:
'END OF FOR FUTURE USE
END SELECT

' ** END OF SUB VERSIONPRNT **
END SUB

SUB YESNO STATIC
'
' SET YES OR NO FLAG 'YS' DEPENDING ON USER INPUT
'
YSNO:
YS$ = INKEY$
IF ((YS$ <> "Y") AND (YS$ <> "y") AND (YS$ <> "N") AND (YS$ <> "n")) THEN GOTO YSNO: 'BAD INPUT
YS = 0: IF ((YS$ = "Y") OR (YS$ = "y")) THEN YS = 1
'
' **** END OF SUB YESNO ****
END SUB

