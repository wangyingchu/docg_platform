package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.realmExample.tools.SHP_DataSourceImport;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;

public class SEATTLE_City_Water_System_Networks_Realm_Generator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String baseLocation="/home/wangychu/Desktop/SEATTLE_City_Water_System_Networks/";
        /*
        doSHPImport(baseLocation+"Adopt_a_Drain_-_Adopted_Drains/Adopt_a_Drain_-_Adopted_Drains.shp","AdoptedDrain");
        doSHPImport(baseLocation+"Adopt_a_Drain_-_Available_Drains/Adopt_a_Drain_-_Available_Drains.shp","AvailableDrain");
        doSHPImport(baseLocation+"Adopt_a_Drain_-_Crowd_Adopted/Adopt_a_Drain_-_Crowd_Adopted.shp","AdoptedCrowdDrain");
        doSHPImport(baseLocation+"Catch_Basin,_Junction_Box,_Sand_Box/Catch_Basin%2C_Junction_Box%2C_Sand_Box.shp","CatchBasin_JunctionBox_SandBox");
        doSHPImport(baseLocation+"Drainage_and_Wastewater_Private_Mainlines/Drainage_and_Wastewater_Private_Mainlines.shp","PrivateDrainageAndWastewaterMainline");
        */
        /*
        doSHPImport(baseLocation+"Drainage_Basins/Drainage_Basins.shp","DrainageBasin");
        doSHPImport(baseLocation+"DWW_Ditches_and_Culverts/DWW_Ditches_and_Culverts.shp","Ditch_Culvert");
        doSHPImport(baseLocation+"DWW_Inlets/DWW_Inlets.shp","Inlet");
        doSHPImport(baseLocation+"DWW_Mainline_Connection_Points_(Wyes)/DWW_Mainline_Connection_Points_(Wyes).shp","MainlineConnectionPoint");
        doSHPImport("/home/wangychu/Desktop/SEATTLE_City_Water_System_Networks/DWW_Mainline_End_Points/DWW_Mainline_End_Points.shp","MainlineEndPoint");
        */
        /*
        doSHPImport(baseLocation+"DWW_Mainlines_(Permitted_Use)/DWW_Mainlines_(Permitted_Use).shp","PermittedUseMainline");
        doSHPImport(baseLocation+"DWW_Mainlines_(Probable_Flow)/DWW_Mainlines_(Probable_Flow).shp","MainlineProbableFlow");
        doSHPImport(baseLocation+"DWW_Pipe_Repairs/DWW_Pipe_Repairs.shp","PipeRepair");
        doSHPImport(baseLocation+"DWW_Storm_Outfalls/DWW_Storm_Outfalls.shp","StormOutfall");
        doSHPImport(baseLocation+"DWW_Swales/DWW_Swales.shp","Swale");
        */
        /*
        doSHPImport(baseLocation+"Flood_Zones_ECA/Flood_Zones_ECA.shp","FloodZone");
        doSHPImport(baseLocation+"Liquefaction_Zones_ECA/Liquefaction_Zones_ECA.shp","LiquefactionZone");
        doSHPImport(baseLocation+"Maintenance_Holes_and_Other_Structures/Maintenance_Holes_and_Other_Structures.shp","MaintenanceHoleAndOtherStructure");
        doSHPImport(baseLocation+"NPDES_Detention_Site_Lines/NPDES_Detention_Site_Lines.shp","DetentionSiteLine");
        doSHPImport(baseLocation+"NPDES_Outfalls/NPDES_Outfalls.shp","Outfall");
        */
        /*
        doSHPImport(baseLocation+"Permeable_Pavement/Permeable_Pavement.shp","PermeablePavement");
        doSHPImport(baseLocation+"Piped_Creek_Watersheds/Piped_Creek_Watersheds.shp","PipedCreekWatershed");
        doSHPImport(baseLocation+"Potential_Landslide_Areas/Potential_Landslide_Areas.shp","PotentialLandslideArea");
        doSHPImport(baseLocation+"Sewer_Classification_Areas/Sewer_Classification_Areas.shp","SewerClassificationArea");
        doSHPImport(baseLocation+"Side_Sewers_and_Laterals/Side_Sewers_and_Laterals.shp","SideSewerAndLateral");
        doSHPImport(baseLocation+"Underdrain_and_Rain_Garden/Underdrain_and_Rain_Garden.shp","UnderDrainAndRainGarden");
        doSHPImport(baseLocation+"Wetlands_ECA/Wetlands_ECA.shp","Wetland");
        */
    }

    private static void doSHPImport(String shpFileLocation,String conceptionKindName) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String pathName = shpFileLocation;
        File file = new File(pathName);
        SHP_DataSourceImport.importSHPDataToConceptionKind(conceptionKindName,true,file,null);
    }
}
