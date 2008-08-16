unit Hips;

interface
uses
  Core,
  Observer,
  SysUtils,
  DataBase,
  Classes;

type
  THips = class
  public

    procedure SendSectorHoloUpdate(Index : Integer; UpDate : TDateTime);
    procedure SendStatusSector(Index : Integer);
    procedure SendSectorWarpCalcUpdate(Index : Integer; UpDate : TDateTime);
    procedure SendAddWarp(Sector : Integer; Warp : Integer);

    end;
implementation
uses
  Global,
  Utility,
  Ansi;

procedure THips.SendSectorHoloUpdate(Index : Integer; UpDate : TDateTime);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Index) + ',');
      TWXServer.Broadcast('"update":"' + DateTimeToStr(UpDate) + '",');
      TWXServer.Broadcast('"explored":"holo"}' + ANSI_REVEAL);
end;

procedure THips.SendSectorWarpCalcUpdate(Index : Integer; UpDate : TDateTime);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Index) + ',');
      TWXServer.Broadcast('"update":"' + DateTimeToStr(UpDate) + '",');
      TWXServer.Broadcast('"constellation":"??? (warp calc only)",');
      TWXServer.Broadcast('"explored":"calc"}' + ANSI_REVEAL);
end;

procedure THips.SendStatusSector(Index : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.status={"sector":' + IntToStr(Index) + '}' + ANSI_REVEAL);
end;

procedure THips.SendAddWarp(Sector : Integer; Warp : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.warp={"from":' + IntToStr(Sector) + ',"to":' + IntToStr(Warp) + '}' + ANSI_REVEAL);
end;

end.
