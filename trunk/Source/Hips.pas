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
  private
    function BoolToJsonStr(Value : Boolean) : string;
  public

    // Send a sector message and its warps as an array
    procedure SendSectorWarps(Index : Integer; Warps : array of Word);

    // Send a commandPrompt message with the current sector
    procedure SendCommandPrompt(Index : Integer);

    // Send a computerPrompt message with the current sector
    procedure SendComputerPrompt(Index : Integer);

    // Sends a warp message, usually from a warp calculation
    procedure SendAddWarp(Sector : Integer; Warp : Integer);

    // Sends a port message with information from a CIM report
    procedure SendCimPort(Sector : Integer; Port : TPort);

    // Sends a sector message with the beacon
    procedure SendSectorBeacon(Sector : Integer; Beacon : string);

    // Sends a port message from the sector display
    procedure SendSectorPort(Sector : Integer; Port : TPort);

    // Sends a planet message from the sector display
    procedure SendSectorPlanet(Sector : Integer; Name : string);

    // Sends a trader message from the sector display
    procedure SendSectorTrader(Sector : Integer; Trader : TTrader);

    // Sends a ship message from the sector display
    procedure SendSectorShip(Sector : Integer; Ship : TShip);

    // Sends a fighters message from the sector display
    procedure SendSectorFighters(Sector : Integer; Figs : TSpaceObject);

    // Sends a sector message with the navhaz information
    procedure SendSectorNavhaz(Sector : Integer; NavHaz : Byte);

    // Sends an armidMines message from the sector display
    procedure SendSectorArmidMines(Sector : Integer; Mines : TSpaceObject);

    // Sends  limpetMines message from the sector display
    procedure SendSectorLimpetMines(Sector : Integer; Mines : TSpaceObject);

    // Sends a port message from the build time
    procedure SendPortBuildTime(Sector : Integer; BuildTime : Byte);

    // Sends a port message from the commerce report
    procedure SendCommerceReport(Sector : Integer; Port : TPort);

    // Sends a noDeployedFighters message when a displayed fighters reports comes
    // up empty
    procedure SendNoDeployedFighters();

    // Sends the sector and port messages for the stardock
    procedure SendStarDockSector(Index : Integer);

    // Sends a sector message containing the constellation name
    procedure SendSectorConstellation(Sector : Integer; Constellation : string);

    // Sends a sector message from a density scan
    procedure SendSectorDensityScan(Index : Integer; Sector : TSector);

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

procedure THips.SendCommerceReport(Sector : Integer; Port : TPort);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Sector));
  TWXServer.Broadcast(',"name":"' + Port.Name + '"');
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

procedure THips.SendCommandPrompt(Index : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.commandPrompt={"sector":' + IntToStr(Index) + '}' + ANSI_REVEAL);
end;

procedure THips.SendComputerPrompt(Index : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.computerPrompt={"sector":' + IntToStr(Index) + '}' + ANSI_REVEAL);
end;

procedure THips.SendAddWarp(Sector : Integer; Warp : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.warp={"from":' + IntToStr(Sector) + ',"to":' + IntToStr(Warp) + '}' + ANSI_REVEAL);
end;

procedure THips.SendSectorBeacon(Sector : Integer; Beacon : string);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Sector) + ',"beacon":"' + Beacon + '"}' + ANSI_REVEAL);
end;

procedure THips.SendSectorConstellation(Sector : Integer; Constellation : string);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Sector) + ',"constallation":"' + Constellation + '"}' + ANSI_REVEAL);
end;

procedure THips.SendSectorNavhaz(Sector : Integer; NavHaz : Byte);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Sector) + ',"navhaz":' + IntToStr(NavHaz) + '}' + ANSI_REVEAL);
end;

procedure THips.SendSectorPlanet(Sector : Integer; Name : string);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.planet={"id":' + IntToStr(Sector) + ',"name":"' + Name + '"}' + ANSI_REVEAL);
end;

procedure THips.SendPortBuildTime(Sector : Integer; BuildTime : Byte);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Sector) + ',"buildTime":' + IntToStr(BuildTime) + '}' + ANSI_REVEAL);
end;

procedure THips.SendNoDeployedFighters();
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.noDeployedFighters={}' + ANSI_REVEAL);
end;

procedure THips.SendSectorPort(Sector : Integer; Port : TPort);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Sector));
  TWXServer.Broadcast(',"dead":' + BoolToJsonStr(Port.Dead));
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

procedure THips.SendSectorLimpetMines(Sector : Integer; Mines : TSpaceObject);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.limpetMines={"sector":' + IntToStr(Sector) + ',');
  TWXServer.Broadcast('"quantity":' + IntToStr(Mines.Quantity) + ',');
  TWXServer.Broadcast('"owner":"' + Mines.Owner + '"');
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendSectorArmidMines(Sector : Integer; Mines : TSpaceObject);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.armidMines={"sector":' + IntToStr(Sector) + ',');
  TWXServer.Broadcast('"quantity":' + IntToStr(Mines.Quantity) + ',');
  TWXServer.Broadcast('"owner":"' + Mines.Owner + '"');
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

procedure THips.SendStarDockSector(Index : Integer);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Index) + ',');
  TWXServer.Broadcast('"beacon":"FedSpace, FedLaw Enforced",');
  TWXServer.Broadcast('"constellation":"The Federation"}' + ANSI_REVEAL);

  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.port={"id":' + IntToStr(Index) + ',');
  TWXServer.Broadcast('"dead":false,');
  TWXServer.Broadcast('"buildTime":0,');
  TWXServer.Broadcast('"name":"Stargate Alpha I",');
  TWXServer.Broadcast('"class":9}' + ANSI_REVEAL);
end;

procedure THips.SendSectorDensityScan(Index : Integer; Sector : TSector);
begin
  TWXServer.Broadcast(ANSI_CONCEAL + 'tw.sector={"id":' + IntToStr(Index) + ',');
  TWXServer.Broadcast('"density":' + IntToStr(Sector.Density) + ',');
  TWXServer.Broadcast('"anomoly":' + BoolToJsonStr(Sector.Anomoly) + ',');
  TWXServer.Broadcast('"navhaz":' + IntToStr(Sector.NavHaz) + ',');
  TWXServer.Broadcast('"numWarps":' + IntToStr(Sector.Warps));
  TWXServer.Broadcast('}' + ANSI_REVEAL);
end;

function THips.BoolToJsonStr(Value : Boolean) : string;
begin
  if (Value) then
    Result := 'true'
  else
    Result := 'false';
end;
end.
