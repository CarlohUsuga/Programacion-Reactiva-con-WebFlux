@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.2.0
@REM Compatible with PowerShell and CMD on Windows
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET "__MVNW_ARG0_NAME__=%~nx0")
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_SAVE_ERRORLEVEL__=
@SET __MVNW_SAVE_CD__=%CD%

@SETLOCAL

@SET APP_HOME=%~dp0
@IF "%APP_HOME:~-1%"=="\" SET "APP_HOME=%APP_HOME:~0,-1%"

@SET PROPERTIES_FILE=%APP_HOME%\.mvn\wrapper\maven-wrapper.properties
@IF NOT EXIST "%PROPERTIES_FILE%" (
  ECHO Cannot find %PROPERTIES_FILE% >&2
  EXIT /B 1
)

@REM Read distributionUrl from properties file
@FOR /F "usebackq tokens=1,* delims==" %%A IN ("%PROPERTIES_FILE%") DO (
  @IF "%%A"=="distributionUrl" SET "DISTRIBUTION_URL=%%B"
)

@IF "%DISTRIBUTION_URL%"=="" (
  ECHO distributionUrl not found in maven-wrapper.properties >&2
  EXIT /B 1
)

@REM Derive the maven distribution folder name from the URL
@FOR %%F IN ("%DISTRIBUTION_URL%") DO SET "DIST_FILENAME=%%~nxF"
@SET "MAVEN_DIST_NAME=%DIST_FILENAME:.zip=%"

@IF "%MAVEN_USER_HOME%"=="" SET "MAVEN_USER_HOME=%USERPROFILE%\.m2\wrapper\dists"
@SET "MAVEN_HOME=%MAVEN_USER_HOME%\%MAVEN_DIST_NAME%"

@REM Paths defined BEFORE the IF block so %VAR% expansion works correctly inside it.
@REM CMD expands %VAR% at block-parse time, not at execution time.
@SET "MAVEN_DIST_DIR=%MAVEN_HOME%"
@SET "TMP_ZIP=%MAVEN_DIST_DIR%\%DIST_FILENAME%"

@REM Download and unzip Maven if the distribution directory does not exist
@IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  ECHO Downloading Maven from %DISTRIBUTION_URL% ...
  IF NOT EXIST "%MAVEN_DIST_DIR%" MKDIR "%MAVEN_DIST_DIR%"

  powershell -Command "& { [Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%TMP_ZIP%' -UseBasicParsing }"
  IF ERRORLEVEL 1 (
    ECHO Failed to download Maven. Check your internet connection. >&2
    EXIT /B 1
  )

  IF NOT EXIST "%TMP_ZIP%" (
    ECHO Download failed: zip file not found at %TMP_ZIP% >&2
    EXIT /B 1
  )
  ECHO Unzipping Maven ...
  powershell -Command "Expand-Archive -Path '%TMP_ZIP%' -DestinationPath '%MAVEN_DIST_DIR%' -Force"
  IF ERRORLEVEL 1 (
    ECHO Failed to unzip Maven. >&2
    EXIT /B 1
  )

  DEL /F /Q "%TMP_ZIP%"

  @REM The zip extracts to a sub-folder; move its contents up one level
  @FOR /D %%D IN ("%MAVEN_HOME%\apache-maven-*") DO (
    XCOPY /E /I /Y "%%D\*" "%MAVEN_HOME%\" >NUL
    RMDIR /S /Q "%%D"
  )

  ECHO Maven installed to %MAVEN_HOME%
)

@SET "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
@IF NOT EXIST "%MAVEN_CMD%" (
  ECHO Could not find %MAVEN_CMD% >&2
  EXIT /B 1
)

@ENDLOCAL & CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
