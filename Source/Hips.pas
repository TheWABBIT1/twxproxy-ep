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

    procedure SendSectorWarps(Index : Integer; Warps : array of Word);
    procedure SendStatusSector(Index : Integer);
    procedure SendAddWarp(Sector : Integer; Warp : Integer);
    procedure SendCimPort(Sector : Integer; Port : TPort);
    procedure SendSectorBeacon(Sector : Integer; Beacon : string);
    procedure SendSectorPort(Sector : Integer; Port : TPort);
    procedure SendSectorPlanet(Sector : Integer; Name : string);
    procedure SendSectorTrader(Sector : Integer; Trader : TTrader);
    procedure SendSectorShip(Sector : Integer; Ship : TShip);
    procedure SendSectorFighters(Sector : Integer; Figs : TSpaceObject);
    procedure SendSectorNavhaz(Sector : Integer; NavHaz : Byte);

    end;
implementation
uses
  Global,
  Utility,
  Ansi;

procedure THips.SendCimPort(Sector : Integer; Port : TPort);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Sector));
  TWXServer.Broadcast(',"productAmounts":{');
    TWXServer.Broadcast('"fuelOre":' + IntToStr(Port.ProductAmount[ptFuelOre]));
    TWXServer.Broadcast(',"organics":' + IntToStr(Port.ProductAmount[ptOrganics]));
    TWXServer.Broadcast(',"equipment":' + IntToStr(Port.ProductAmount[ptEquipment]));
  TWXServer.Broadcast('},"productPercents":{');
    TWXServer.Broadcast('"fuelOre":' + IntToStr(Port.ProductPercent[ptFuelOre]));
    TWXServer.Broadcast(',"organics":' + IntToStr(Port.ProductPercent[ptOrganics]));
    TWXServer.Broadcast(',"equipment":' + IntToStr(Port.ProductPercent[ptEquipment]));
  TWXServer.Broadcast('},"class":' + IntToStr(Port.ClassIndex));
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendSectorWarps(Index : Integer; Warps : array of Word);
var
  i : Integer;
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Index) + ',"warps":[');
  for i:=Low(Warps) to High(Warps) do
  begin
    if (Warps[i] > 0) then
    begin
      TWXServer.Broadcast(IntToStr(Warps[i]));
      if (i < High(Warps)) and (Warps[i + 1] > 0) then
      begin
        TWXServer.Broadcast(',');
      end
    end;
  end;
  TWXServer.Broadcast(']}' + ANSI_REVEAL);
end;

procedure THips.SendStatusSector(Index : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.status={"sector":' + IntToStr(Index) + '}' + ANSI_REVEAL);
end;

procedure THips.SendAddWarp(Sector : Integer; Warp : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.warp={"from":' + IntToStr(Sector) + ',"to":' + IntToStr(Warp) + '}' + ANSI_REVEAL);
end;

procedure THips.SendSectorBeacon(Sector : Integer; Beacon : string);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Sector) + ',"beacon":"' + Beacon + '"}' + ANSI_REVEAL);
end;

procedure THips.SendSectorNavhaz(Sector : Integer; NavHaz : Byte);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Sector) + ',"navhaz":' + IntToStr(NavHaz) + '}' + ANSI_REVEAL);
end;

procedure THips.SendSectorPlanet(Sector : Integer; Name : string);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.planet={"id":' + IntToStr(Sector) + ',"name":"' + Name + '"}' + ANSI_REVEAL);
end;

procedure THips.SendSectorPort(Sector : Integer; Port : TPort);
var
  dead : string;
begin
  if (Port.Dead) then
    dead := 'true'
  else
    dead := 'false';

  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Sector));
  TWXServer.Broadcast(',"dead":' + dead);
  if not Port.Dead then
  begin
    TWXServer.Broadcast(',"class":' + IntToStr(Port.ClassIndex));
    TWXServer.Broadcast(',"buildTime":0');
    TWXServer.Broadcast(',"name":"' + Port.Name + '"');
  end;
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendSectorTrader(Sector : Integer; Trader : TTrader);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.trader={"sector":' + IntToStr(Sector) + ',');
  TWXServer.Broadcast('"name":"' + Trader.Name + '",');
  TWXServer.Broadcast('"shipType":"' + Trader.ShipType + '",');
  TWXServer.Broadcast('"figs":' + IntToStr(Trader.Figs));
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendSectorShip(Sector : Integer; Ship : TShip);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.ship={"sector":' + IntToStr(Sector) + ',');
  TWXServer.Broadcast('"name":"' + Ship.Name + '",');
  TWXServer.Broadcast('"owner":"' + Ship.Owner + '",');
  TWXServer.Broadcast('"shipType":"' + Ship.ShipType + '",');
  TWXServer.Broadcast('"figs":' + IntToStr(Ship.Figs));
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendSectorFighters(Sector : Integer; Figs : TSpaceObject);
var
  figType : string;
begin
  case Figs.FigType of
    ftToll      : figType := 'toll';
    ftDefensive : figType := 'defensive';
    ftOffensive : figType := 'offensive';
  end;
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.fighters={"sector":' + IntToStr(Sector) + ',');
  TWXServer.Broadcast('"quantity":' + IntToStr(Figs.Quantity) + ',');
  TWXServer.Broadcast('"owner":"' + Figs.Owner + '",');
  TWXServer.Broadcast('"figType":"' + figType + '"');
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;


end.
