unit IniConfig;

interface
uses
  SysUtils,
  inifiles,
  Classes,
  Database;

type
  TIniConfig = class
  private
    procedure CreateDatabase(ProgramDir : string; Game : Char; Description : string; DatabaseName : string; IniFile : TIniFile);
  public
    function LoadConfig(ProgramDir : string; FileName : string) : Boolean;
  end;

implementation
uses
  Global,
  Utility;

function TIniConfig.LoadConfig(ProgramDir : string; FileName: string) : Boolean;
var
  IniFile : TIniFile;
  Game : Char;
  Description : string;
  DatabaseName : string;

begin
  IniFile := TIniFile.Create(FileName);

  TWXServer.ListenPort := IniFile.ReadInteger('TWXServer','Port',23);
  TWXServer.AcceptExternal := IniFile.ReadBool('TWXServer','AcceptExternal',FALSE);
  TWXClient.Reconnect := IniFile.ReadBool('TWXClient','Reconnect',TRUE);

  Game := IniFile.ReadString('TWXDatabase','Game','A')[1];
  Description := IniFile.ReadString('TWXDatabase','Description',Game);
  DatabaseName := 'data\' + Description + '.xdb';
  SetCurrentDir(ProgramDir);
  if not FileExists(DatabaseName) then
    CreateDatabase(ProgramDir, Game, Description, DatabaseName, IniFile);

  TWXDatabase.DatabaseName := DatabaseName;
  TWXGUI.FirstLoad := FALSE;

  IniFile.Free;
end;

procedure TIniConfig.CreateDatabase(ProgramDir : string; Game : Char; Description : string; DatabaseName : string; IniFile : TIniFile);
var
  Head : PDataHeader;
begin

  Head := GetBlankHeader;
  Head^.Description := Description;
  Head^.Address := IniFile.ReadString('TWXDatabase','Address','localhost');
  Head^.Sectors := IniFile.ReadInteger('TWXDatabase','Sectors', 5000);
  Head^.Port := IniFile.ReadInteger('TWXDatabase','Port',2003);

  if IniFile.ReadBool('TWXDatabase','UseLogin', FALSE) then
  begin
    Head^.UseLogin := TRUE;
    Head^.LoginName := IniFile.ReadString('TWXDatabase','LoginName','TestTrader');
    Head^.Password := IniFile.ReadString('TWXDatabase','Server','TestTrader');
    Head^.Game := Game;
    Head^.LoginScript := IniFile.ReadString('TWXDatabase','LoginScript','scripts\1_Login.ts');
  end
  else
    Head^.UseLogin := FALSE;


  SetCurrentDir(ProgramDir);
  try
    TWXDatabase.CreateDatabase(DatabaseName, Head^);
  except
    // Should handle this better
    //MessageDlg('An error occured while trying to create the database', mtError, [mbOK], 0);
    Exit;
  end;
  FreeMem(Head);
end;

end.
