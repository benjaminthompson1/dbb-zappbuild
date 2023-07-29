        IDENTIFICATION DIVISION.
        PROGRAM-ID. HELLO.
        DATE-COMPILED.
        ENVIRONMENT DIVISION.
        CONFIGURATION SECTION.
        SOURCE-COMPUTER. Z25A.
        OBJECT-COMPUTER. Z25A.
        DATA DIVISION.
        WORKING-STORAGE SECTION.
        01 WS-CURRENT-DATE-DATA.
           05  WS-CURRENT-DATE.
                10  WS-CURRENT-YEAR         PIC 9(04).
                10  WS-CURRENT-MONTH        PIC 9(02).
                10  WS-CURRENT-DAY          PIC 9(02).
           05  WS-CURRENT-TIME.
                10  WS-CURRENT-HOURS        PIC 9(02).
                10  WS-CURRENT-MINUTE       PIC 9(02).
                10  WS-CURRENT-SECOND       PIC 9(02).
                10  WS-CURRENT-MILLISECONDS PIC 9(02).
        PROCEDURE DIVISION.
        MAIN-LINE SECTION.
        0000.
           PERFORM A-MAIN.
        9999.
           STOP RUN.
         A-MAIN SECTION.
         A-100.
             MOVE FUNCTION CURRENT-DATE to WS-CURRENT-DATE-DATA
             DISPLAY 'Current Date - 'WS-CURRENT-MONTH'/'WS-CURRENT-DAY
                '/'WS-CURRENT-YEAR.
             DISPLAY 'Current Time - 'WS-CURRENT-HOURS':'
                WS-CURRENT-MINUTE.
             DISPLAY ' '.
             DISPLAY 'HELLO WORLD'.