package researchDataPrepare

import com.viewfunction.docg.analysisProvider.feature.common.GlobalDataAccessor
import com.viewfunction.docg.analysisProvider.providerApplication.AnalysisProviderApplicationUtil
import com.viewfunction.docg.analysisProvider.tools.dataMaintain.SpatialDataMaintainUtil

import java.io.File
import java.util

object SeattleGISDataLoad extends App{

  val sparkApplicationName = AnalysisProviderApplicationUtil.getApplicationProperty("sparkApplicationName")
  val sparkMasterLocation = AnalysisProviderApplicationUtil.getApplicationProperty("sparkMasterLocation")
  val sparkExecutorInstanceNumber = AnalysisProviderApplicationUtil.getApplicationProperty("sparkExecutorInstanceNumber")
  val globalDataAccessor = new GlobalDataAccessor(sparkApplicationName,sparkMasterLocation,sparkExecutorInstanceNumber)
  val spatialDataMaintainUtil = new SpatialDataMaintainUtil

  val seattleGISDataSHPInfoMapping = new util.HashMap[String,String]

  /*
  //Transportation Data
  seattleGISDataSHPInfoMapping.put("Street","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Seattle_Streets/Seattle_Streets.shp")
  seattleGISDataSHPInfoMapping.put("Bridge","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Bridges/Bridges.shp")
  seattleGISDataSHPInfoMapping.put("CommonPlace","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Common_Place_Names_(CPN)/Common_Place_Names_(CPN).shp")
  seattleGISDataSHPInfoMapping.put("StreetCarStation","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Streetcar_Stations/Streetcar_Stations.shp")
  seattleGISDataSHPInfoMapping.put("TrafficBeacon","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Traffic_Beacons/Traffic_Beacons.shp")
  seattleGISDataSHPInfoMapping.put("TrafficCircle","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Traffic_Circles/Traffic_Circles.shp")
  seattleGISDataSHPInfoMapping.put("StreetSign","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Street_Signs/Street_Signs.shp")
  seattleGISDataSHPInfoMapping.put("TrafficCamera","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Traffic_Cameras/Traffic_Cameras.shp")
  seattleGISDataSHPInfoMapping.put("TrafficDetector","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Traffic_Detectors/Traffic_Detectors.shp")
  seattleGISDataSHPInfoMapping.put("Crosswalk","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Marked_Crosswalks/Marked_Crosswalks.shp")
  seattleGISDataSHPInfoMapping.put("BikeFacility","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Existing_Bike_Facilities/Existing_Bike_Facilities.shp")
  seattleGISDataSHPInfoMapping.put("RadarSpeedSign","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Radar_Speed_Signs/Radar_Speed_Signs.shp")
  seattleGISDataSHPInfoMapping.put("StreetCarLine","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Streetcar_Lines/Streetcar_Lines.shp")
  seattleGISDataSHPInfoMapping.put("StreetNetwork","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Street_Network_Database_(SND)/Street_Network_Database_(SND).shp")
  seattleGISDataSHPInfoMapping.put("TrafficSignal","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Traffic_Signals/Traffic_Signals.shp")
  seattleGISDataSHPInfoMapping.put("StreetEnd","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Street_Ends_(Shoreline)/Street_Ends_(Shoreline).shp")
  seattleGISDataSHPInfoMapping.put("CrashCushion","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Crash_Cushions/Crash_Cushions.shp")
  seattleGISDataSHPInfoMapping.put("DynamicMessageSign","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Dynamic_Message_Signs/Dynamic_Message_Signs.shp")
  seattleGISDataSHPInfoMapping.put("FreightNetwork","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Freight_Network/Freight_Network.shp")
  seattleGISDataSHPInfoMapping.put("DowntownAlley","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Greater_Downtown_Alleys-shp/Greater_Downtown_Alleys.shp")
  seattleGISDataSHPInfoMapping.put("Guardrail","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Guardrails/Guardrails.shp")
  seattleGISDataSHPInfoMapping.put("OneWayStreet","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/One-Way_Streets/One-Way_Streets.shp")
  seattleGISDataSHPInfoMapping.put("PaidAreaCurbspace","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Paid_Area_Curbspaces-shp/Paid_Area_Curbspaces.shp")
  seattleGISDataSHPInfoMapping.put("PayStation","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Pay_Stations/Pay_Stations.shp")
  seattleGISDataSHPInfoMapping.put("PeakHourParkingRestriction","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Peak_Hour_Parking_Restrictions/Peak_Hour_Parking_Restrictions.shp")
  seattleGISDataSHPInfoMapping.put("RetainingWall","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Retaining_Walls/Retaining_Walls.shp")
  seattleGISDataSHPInfoMapping.put("Sidewalk","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Sidewalks/Sidewalks.shp")
  seattleGISDataSHPInfoMapping.put("Stairway","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Stairways/Stairways.shp")
  seattleGISDataSHPInfoMapping.put("CurbRamp","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Transportation/Curb_Ramps_Point/Curb_Ramps_Point.shp")
  */

  //Boundaries Data
  /*
  seattleGISDataSHPInfoMapping.put("BuildingOutline","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Building_Outlines_2012-shp/Building_Outlines_2012.shp")
  seattleGISDataSHPInfoMapping.put("CadastralControlLine","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Cadastral_Control_Lines/Cadastral_Control_Lines.shp")
  seattleGISDataSHPInfoMapping.put("CensusBlockGroup","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Census_Block_Groups_2010/Census_Block_Groups_2010.shp")
  seattleGISDataSHPInfoMapping.put("CensusBlock","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Census_Blocks_2010/Census_Blocks_2010.shp")
  seattleGISDataSHPInfoMapping.put("CensusTract","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Census_Tracts_2010/Census_Tracts_2010.shp")
  seattleGISDataSHPInfoMapping.put("CityShoreline","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/City_of_Seattle_Shoreline/City_of_Seattle_Shoreline.shp")
  seattleGISDataSHPInfoMapping.put("PropertyManagementArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/City_Property_-_Property_Management_Areas/City_Property_-_Property_Management_Areas.shp")
  seattleGISDataSHPInfoMapping.put("PropertySubjectParcel","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/City_Property_-_Subject_Parcels/City_Property_-_Subject_Parcels.shp")
  seattleGISDataSHPInfoMapping.put("CommunityReportingArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Community_Reporting_Areas/Community_Reporting_Areas.shp")
  seattleGISDataSHPInfoMapping.put("ContourLine","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Contour_Lines_1993-shp/Contour_Lines_1993.shp")
  seattleGISDataSHPInfoMapping.put("CouncilDistrict","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Council_Districts/Council_Districts.shp")
  seattleGISDataSHPInfoMapping.put("DesignReviewEquityArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Design_Review_Equity_Areas/Design_Review_Equity_Areas.shp")
  seattleGISDataSHPInfoMapping.put("MunicipalBoundary","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Municipal_Boundaries/Municipal_Boundaries.shp")
  seattleGISDataSHPInfoMapping.put("PLSSLine","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/PLSS_Line/PLSS_Line.shp")
  seattleGISDataSHPInfoMapping.put("CityLimit","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Seattle_City_Limits/Seattle_City_Limits.shp")
  seattleGISDataSHPInfoMapping.put("ZipCodeArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/Zip_Codes/Zip_Codes.shp")
  */

  //Environment Data
  /*
  seattleGISDataSHPInfoMapping.put("StreetTree","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Trees/Trees.shp")
  seattleGISDataSHPInfoMapping.put("DirectDischargeArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Direct_Discharge_Areas/Direct_Discharge_Areas.shp")
  seattleGISDataSHPInfoMapping.put("DrainageBasin","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Drainage_Basins/Drainage_Basins.shp")
  seattleGISDataSHPInfoMapping.put("Ditch_Culvert","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/DWW_Ditches_and_Culverts/DWW_Ditches_and_Culverts.shp")
  seattleGISDataSHPInfoMapping.put("Inlet","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/DWW_Inlets/DWW_Inlets.shp")
  seattleGISDataSHPInfoMapping.put("StormOutfall","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/DWW_Storm_Outfalls/DWW_Storm_Outfalls.shp")
  seattleGISDataSHPInfoMapping.put("Swale","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/DWW_Swales/DWW_Swales.shp")
  seattleGISDataSHPInfoMapping.put("FloodZone","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Flood_Zones_ECA/Flood_Zones_ECA.shp")
  seattleGISDataSHPInfoMapping.put("GSI_AppurtenancePoint","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/GSI_Appurtenance_Points/GSI_Appurtenance_Points.shp")
  seattleGISDataSHPInfoMapping.put("GSI_Facility","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/GSI_Facility_Footprints/GSI_Facility_Footprints.shp")
  seattleGISDataSHPInfoMapping.put("GSI_Point","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/GSI_Points_A/GSI_Points.shp")
  seattleGISDataSHPInfoMapping.put("GSI_Footprint","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Other_GSI_Footprints/Other_GSI_Footprints.shp")
  seattleGISDataSHPInfoMapping.put("HistoricLandslideLocation","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Historic_Landslide_Locations_ECA/Historic_Landslide_Locations_ECA.shp")
  seattleGISDataSHPInfoMapping.put("LandfillArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Landfill_Areas/Landfill_Areas.shp")
  seattleGISDataSHPInfoMapping.put("LiquefactionZone","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Liquefaction_Zones_ECA/Liquefaction_Zones_ECA.shp")
  seattleGISDataSHPInfoMapping.put("NPDES_DetentionSite","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/NPDES_Detention_Site_Polygons/NPDES_Detention_Site_Polygons.shp")
  seattleGISDataSHPInfoMapping.put("NPDES_DetentionSiteLine","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/NPDES_Detention_Site_Lines/NPDES_Detention_Site_Lines.shp")
  seattleGISDataSHPInfoMapping.put("NPDES_Outfall","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/NPDES_Outfalls/NPDES_Outfalls.shp")
  seattleGISDataSHPInfoMapping.put("NPDES_SystemControl","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/NPDES_System_Control/NPDES_System_Control.shp")
  seattleGISDataSHPInfoMapping.put("PeatSettlementProneArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Peat_Settlement-Prone_Areas/Peat_Settlement-Prone_Areas.shp")
  seattleGISDataSHPInfoMapping.put("PermeablePavement","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Permeable_Pavement/Permeable_Pavement.shp")
  seattleGISDataSHPInfoMapping.put("PipedCreekWatershed","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Piped_Creek_Watersheds/Piped_Creek_Watersheds.shp")
  seattleGISDataSHPInfoMapping.put("PotentialLandslideArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Potential_Landslide_Areas/Potential_Landslide_Areas.shp")
  seattleGISDataSHPInfoMapping.put("SteepSlope","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Steep_Slope_ECA/Steep_Slope_ECA.shp")
  seattleGISDataSHPInfoMapping.put("TreeCanopy","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Tree_Canopy_2016/Tree_Canopy_2016.shp")
  seattleGISDataSHPInfoMapping.put("UnderDrain_RainGarden","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Underdrain_and_Rain_Garden/Underdrain_and_Rain_Garden.shp")
  seattleGISDataSHPInfoMapping.put("UrbanCreekWatershed","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Urban_Creek_Watersheds/Urban_Creek_Watersheds.shp")
  seattleGISDataSHPInfoMapping.put("UrbanWatercourse","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Urban_Watercourses/Urban_Watercourses.shp")
  seattleGISDataSHPInfoMapping.put("Wetland","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Wetlands_ECA/Wetlands_ECA.shp")
  seattleGISDataSHPInfoMapping.put("WildlifeCorridor","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Environment/Wildlife_Corridors_ECA/Wildlife_Corridors_ECA.shp")
  */

  //Utilities Data
  /*
  seattleGISDataSHPInfoMapping.put("AdoptedDrain","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Adopt_a_Drain_-_Adopted_Drains/Adopt_a_Drain_-_Adopted_Drains.shp")
  seattleGISDataSHPInfoMapping.put("AvailableDrain","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Adopt_a_Drain_-_Available_Drains/Adopt_a_Drain_-_Available_Drains.shp")
  seattleGISDataSHPInfoMapping.put("AdoptedCrowdDrain","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Adopt_a_Drain_-_Crowd_Adopted/Adopt_a_Drain_-_Crowd_Adopted.shp")
  seattleGISDataSHPInfoMapping.put("CapacityConstrainedDrainageSystem","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Capacity_Constrained_Drainage_Systems/Capacity_Constrained_Drainage_Systems.shp")
  seattleGISDataSHPInfoMapping.put("CatchBasin_JunctionBox_SandBox","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Catch_Basin,_Junction_Box,_Sand_Box/Catch_Basin%2C_Junction_Box%2C_Sand_Box.shp")
  seattleGISDataSHPInfoMapping.put("CommunityEmergencyHub","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/CommunityEmergencyHubs/CommunityEmergencyHubs.shp")
  seattleGISDataSHPInfoMapping.put("CitywideGreenStormInfrastructure","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Citywide_Green_Storm_Infrastructure/Citywide_Green_Storm_Infrastructure.shp")
  seattleGISDataSHPInfoMapping.put("CSO_Basin","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/CSO_Basins/CSO_Basins.shp")
  seattleGISDataSHPInfoMapping.put("DrainageAndWastewaterPrivateMainline","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Drainage_and_Wastewater_Private_Mainlines/Drainage_and_Wastewater_Private_Mainlines.shp")
  seattleGISDataSHPInfoMapping.put("DWW_MainlineConnectionPoint","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Mainline_Connection_Points_(Wyes)/DWW_Mainline_Connection_Points_(Wyes).shp")
  seattleGISDataSHPInfoMapping.put("DWW_MainlineEndPoint","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Mainline_End_Points/DWW_Mainline_End_Points.shp")
  seattleGISDataSHPInfoMapping.put("DWW_PermittedUseMainline","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Mainlines_(Permitted_Use)/DWW_Mainlines_(Permitted_Use).shp")
  seattleGISDataSHPInfoMapping.put("DWW_MainlineProbableFlow","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Mainlines_(Probable_Flow)/DWW_Mainlines_(Probable_Flow).shp")
  seattleGISDataSHPInfoMapping.put("DWW_NonMainlineMiscStructure","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Non-Mainline_Misc_Structures/DWW_Non-Mainline_Misc_Structures.shp")
  seattleGISDataSHPInfoMapping.put("RemoteWatershed","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Remote_Watersheds/Remote_Watersheds.shp")
  seattleGISDataSHPInfoMapping.put("DWW_PipeRepair","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/DWW_Pipe_Repairs/DWW_Pipe_Repairs.shp")
  seattleGISDataSHPInfoMapping.put("MaintenanceHoleAndOtherStructure","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Maintenance_Holes_and_Other_Structures/Maintenance_Holes_and_Other_Structures.shp")
  seattleGISDataSHPInfoMapping.put("NonMainlineDetention","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Non-Mainline_Detention_Polygons/Non-Mainline_Detention_Polygons.shp")
  seattleGISDataSHPInfoMapping.put("Park_Recreation","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Seattle Parks and Recreation GIS Map Layer Shapefile - Park Boundary/geo_export_082187a2-56b7-4e45-a11c-58198c390817.shp")
  seattleGISDataSHPInfoMapping.put("CityMasterAddress","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Master_Address_File_(MAF)/Master_Address_File_(MAF).shp")
  seattleGISDataSHPInfoMapping.put("SewerClassificationArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Sewer_Classification_Areas/Sewer_Classification_Areas.shp")
  seattleGISDataSHPInfoMapping.put("SideAndLateralSewer","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/Side_Sewers_and_Laterals/Side_Sewers_and_Laterals.shp")
  seattleGISDataSHPInfoMapping.put("SPU_RetailWaterServiceArea","/home/wangychu/Desktop/SEATTLE_GIS_DATA/Utilities/SPU_Retail_Water_Service_Area-shp/SPU_Retail_Water_Service_Area.shp")
  */
  seattleGISDataSHPInfoMapping.forEach((objectType,shpLocation)=>{
  val shpFile = new File(shpLocation)
  val shpParseResult = spatialDataMaintainUtil.parseSHPData(shpFile,null)
  spatialDataMaintainUtil.duplicateSpatialDataInfoToDataSlice(globalDataAccessor,shpParseResult,objectType,"defaultGroup",true,null)
  })

  Thread.sleep(5000)
  globalDataAccessor.close()
}
