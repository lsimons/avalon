;NSIS Modern User Interface version 1.63
;Welcome/Finish Page Example Script
;Written by Joost Verburg

!define MUI_PRODUCT "Merlin"
!define MUI_VERSION "3.2"

!include "MUI.nsh"
  
;--------------------------------
;Configuration

  ;General
  OutFile "..\..\target\merlin-install-3.2.exe"

  ;Folder selection page
  InstallDir "$PROGRAMFILES\${MUI_PRODUCT}"
  
  ;Remember install folder
  InstallDirRegKey HKCU "Software\${MUI_PRODUCT}" ""

;--------------------------------
;Modern UI Configuration

  !define MUI_WELCOMEPAGE
  !define MUI_LICENSEPAGE
  !define MUI_COMPONENTSPAGE
  !define MUI_DIRECTORYPAGE
  !define MUI_FINISHPAGE
  
  !define MUI_ABORTWARNING
  
  !define MUI_UNINSTALLER
  !define MUI_UNCONFIRMPAGE

  
;--------------------------------
;Languages
 
  !insertmacro MUI_LANGUAGE "English"
  
;--------------------------------
;Language Strings

  ;Description
  LangString DESC_SecCopyUI ${LANG_ENGLISH} "Merlin core system resources."

;--------------------------------
;Data
  
  LicenseData "./../etc/license.txt"
  
;--------------------------------
;Reserve Files

  ;Things that need to be extracted on first (keep these lines before any File command!)
  ;Only useful for BZIP2 compression
  !insertmacro MUI_RESERVEFILE_WELCOMEFINISHPAGE

;--------------------------------
;Installer Sections

Section "Merlin Kernel" SecCopyUI

  SectionIn RO

  ;Add your stuff here

  SetOutPath "$INSTDIR"
  File /r "..\..\..\..\target\merlin\**"

  ;Store install folder
  WriteRegStr HKCU "Software\${MUI_PRODUCT}" "" $INSTDIR
    
  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

SectionEnd

Section "Start Menu Shortcuts"

  CreateDirectory "$SMPROGRAMS\${MUI_PRODUCT}"
  CreateShortCut "$SMPROGRAMS\${MUI_PRODUCT}\Uninstall.lnk" "$INSTDIR\uninstall.exe" "" "$INSTDIR\uninstall.exe" 0
  
  CreateShortCut "$SMPROGRAMS\${MUI_PRODUCT}\Readme.lnk" "$INSTDIR\README.txt" "" "$INSTDIR\README.txt" 0

SectionEnd


;--------------------------------
;Descriptions

!insertmacro MUI_FUNCTIONS_DESCRIPTION_BEGIN
  !insertmacro MUI_DESCRIPTION_TEXT ${SecCopyUI} $(DESC_SecCopyUI)
!insertmacro MUI_FUNCTIONS_DESCRIPTION_END

;--------------------------------
;Uninstaller Section

Section "Uninstall"

  ;Add your stuff here

  Delete "$INSTDIR\Uninstall.exe"
  Delete "$INSTDIR\INSTALL.TXT"
  Delete "$INSTDIR\LICENSE.TXT"
  Delete "$INSTDIR\README.TXT"

  RMDir /r "$INSTDIR\bin"
  RMDir /r "$INSTDIR\config"
  RMDir /r "$INSTDIR\system"
  RMDir "$INSTDIR"

  ; remove shortcuts, if any
  Delete "$SMPROGRAMS\${MUI_PRODUCT}\*.*"
  RMDir "$SMPROGRAMS\${MUI_PRODUCT}"

  DeleteRegKey /ifempty HKCU "Software\${MUI_PRODUCT}"

  !insertmacro MUI_UNFINISHHEADER

SectionEnd