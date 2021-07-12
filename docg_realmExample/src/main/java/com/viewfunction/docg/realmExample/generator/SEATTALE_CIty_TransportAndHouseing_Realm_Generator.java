package com.viewfunction.docg.realmExample.generator;

import com.viewfunction.docg.coreRealm.realmServiceCore.exception.CoreRealmServiceRuntimeException;
import com.viewfunction.docg.realmExample.tools.SHP_DataSourceImport;
import org.opengis.referencing.FactoryException;

import java.io.File;
import java.io.IOException;

public class SEATTALE_CIty_TransportAndHouseing_Realm_Generator {

    public static void main(String[] args) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String baseLocation="/home/wangychu/Desktop/SEATTALE_CIty_TransportAndHouseing/";
        //doSHPImport(baseLocation+"Blockface/Blockface.shp", "Blockface");
        //doSHPImport(baseLocation+"Bridges/Bridges.shp", "Bridge");
        //doSHPImport(baseLocation+"Building_Outlines_2015/Building_Outlines_2015.shp", "Building");
        //doSHPImport(baseLocation+"Common_Place_Names_(CPN)/Common_Place_Names_(CPN).shp", "CPN");
        //doSHPImport(baseLocation+"Community_Reporting_Areas/Community_Reporting_Areas.shp", "CommunityReportingArea");
        //doSHPImport(baseLocation+"Crash_Cushions/Crash_Cushions.shp", "CrashCushion");
        //doSHPImport(baseLocation+"Curb_Ramps/Curb_Ramps.shp", "CurbRamp");
        //doSHPImport(baseLocation+"Dynamic_Message_Signs/Dynamic_Message_Signs.shp", "DynamicMessageSign");
        //doSHPImport(baseLocation+"Existing_Bike_Facilities/Existing_Bike_Facilities.shp", "BikeFacility");
        //doSHPImport(baseLocation+"Freight_Network/Freight_Network.shp", "FreightNetwork");
        //doSHPImport(baseLocation+"Greater_Downtown_Alleys-shp/Greater_Downtown_Alleys.shp", "GreaterDowntownAlley");
        //doSHPImport(baseLocation+"Guardrails/Guardrails.shp", "Guardrail");
        //doSHPImport(baseLocation+"Marked_Crosswalks/Marked_Crosswalks.shp", "MarkedCrosswalk");
        //doSHPImport(baseLocation+"Municipal_Boundaries/Municipal_Boundaries.shp", "MunicipalBoundary");
        //doSHPImport(baseLocation+"One-Way_Streets/One-Way_Streets.shp", "OneWayStreet");
        //doSHPImport(baseLocation+"Paid_Area_Curbspaces-shp/Paid_Area_Curbspaces.shp", "PaidAreaCurbspace");
        //doSHPImport(baseLocation+"Pay_Stations/Pay_Stations.shp", "PayStation");
        //doSHPImport(baseLocation+"Peak_Hour_Parking_Restrictions/Peak_Hour_Parking_Restrictions.shp", "PeakHourParkingRestriction");
        //doSHPImport(baseLocation+"Radar_Speed_Signs/Radar_Speed_Signs.shp", "RadarSpeedSign");
        //doSHPImport(baseLocation+"Retaining_Walls/Retaining_Walls.shp", "RetainingWall");
        //doSHPImport(baseLocation+"Seattle_City_Limits/Seattle_City_Limits.shp", "SeattleCityLimits");
        //doSHPImport(baseLocation+"Seattle Parks and Recreation GIS Map Layer Shapefile - Park Boundary/geo_export_082187a2-56b7-4e45-a11c-58198c390817.shp", "Park_Recreation");
        //doSHPImport(baseLocation+"Seattle_Streets/Seattle_Streets.shp", "Street");
        //doSHPImport(baseLocation+"Sidewalks/Sidewalks.shp", "Sidewalk");
        //doSHPImport(baseLocation+"Stairways/Stairways.shp", "Stairway");
        //doSHPImport(baseLocation+"Streetcar_Lines/Streetcar_Lines.shp", "StreetCarLine");
        //doSHPImport(baseLocation+"Streetcar_Stations/Streetcar_Stations.shp", "StreetCarStation");
        //doSHPImport(baseLocation+"Street_Ends_(Shoreline)/Street_Ends_(Shoreline).shp", "StreetEnd");
        //doSHPImport(baseLocation+"Street_Ends_(Shoreline)/Street_Ends_(Shoreline).shp", "StreetEnd");
        //doSHPImport(baseLocation+"Street_Network_Database_(SND)/Street_Network_Database_(SND).shp", "StreetNetwork");
        //doSHPImport(baseLocation+"Street_Signs/Street_Signs.shp", "StreetSign");
        //doSHPImport(baseLocation+"Traffic_Beacons/Traffic_Beacons.shp", "TrafficBeacon");
        //doSHPImport(baseLocation+"Traffic_Cameras/Traffic_Cameras.shp", "TrafficCamera");
        //doSHPImport(baseLocation+"Traffic_Circles/Traffic_Circles.shp", "TrafficCircle");
        //doSHPImport(baseLocation+"Traffic_Detectors/Traffic_Detectors.shp", "TrafficDetector");
        //doSHPImport(baseLocation+"Traffic_Signals/Traffic_Signals.shp", "TrafficSignal");
        //doSHPImport(baseLocation+"Tree_Canopy_2016/Tree_Canopy_2016.shp", "TreeCanopy");
        //doSHPImport(baseLocation+"Trees/Trees.shp", "Tree");
        //doSHPImport(baseLocation+"Wildlife_Corridors_ECA/Wildlife_Corridors_ECA.shp", "WildlifeCorridor");
        //doSHPImport(baseLocation+"Zip_Codes/Zip_Codes.shp", "ZipCodeArea");
    }

    private static void doSHPImport(String shpFileLocation,String conceptionKindName) throws CoreRealmServiceRuntimeException, FactoryException, IOException {
        String pathName = shpFileLocation;
        File file = new File(pathName);
        SHP_DataSourceImport.importSHPDataToConceptionKind(conceptionKindName,true,file,null);
    }
}
