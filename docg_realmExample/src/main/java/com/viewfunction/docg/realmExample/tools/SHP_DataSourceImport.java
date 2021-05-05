package com.viewfunction.docg.realmExample.tools;

import org.geotools.data.FileDataStore;
import org.geotools.data.FileDataStoreFinder;
import org.geotools.data.shapefile.ShapefileDataStore;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.feature.FeatureCollection;
import org.geotools.referencing.CRS;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.Property;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.GeometryType;
import org.opengis.feature.type.Name;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class SHP_DataSourceImport {

    public static void main0(String[] args){
        SHP_DataStructureParse("/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/City_Property_-_Property_Management_Areas/City_Property_-_Property_Management_Areas.shp");
    }

    public static void main(String[] args) throws IOException, FactoryException {

        System.out.println(CRS.decode("epsg:4326").getCoordinateSystem().getName());


        String pathName = "/home/wangychu/Desktop/SEATTLE_GIS_DATA/Boundaries/City_Property_-_Property_Management_Areas/City_Property_-_Property_Management_Areas.shp";
        File file = new File(pathName);

        // 读取到数据存储中
        FileDataStore dataStore = FileDataStoreFinder.getDataStore(file);
        // 获取特征资源
        SimpleFeatureSource simpleFeatureSource = dataStore.getFeatureSource();
        // 要素集合
        SimpleFeatureCollection simpleFeatureCollection = simpleFeatureSource.getFeatures();

        // 要素数量
        int featureSize = simpleFeatureCollection.size();
        // 获取要素迭代器
        SimpleFeatureIterator featureIterator = simpleFeatureCollection.features();
        if(featureIterator.hasNext()){
            // 要素对象
            SimpleFeature feature = featureIterator.next();
            // 要素属性信息，名称，值，类型
            List<Property> propertyList = (List<Property>) feature.getValue();
            for(Property property : propertyList){
                System.out.println("属性名称：" + property.getName());
                System.out.println("属性值：" + property.getValue());
                System.out.println("属性类型：" + property.getType());
                System.out.println();
            }

            // 要素属性信息
            List<Object> featureAttributes = feature.getAttributes();

            // 要素geometry的类型和坐标，如点，线，面及其组成的坐标
            Object geometryText = feature.getDefaultGeometry();

            // geometry属性
            GeometryAttribute geometryAttribute = feature.getDefaultGeometryProperty();
            // 获取坐标参考系信息
            CoordinateReferenceSystem coordinateReferenceSystem = geometryAttribute.getDescriptor().getCoordinateReferenceSystem();

            // geometry类型
            GeometryType geometryType = geometryAttribute.getType();
            // geometry类型名称
            Name name = geometryType.getName();

            System.out.println("要素数量："+ featureSize);
            System.out.println("要素属性：" + featureAttributes);
            System.out.println("要素geometry位置信息：" + geometryText);
            System.out.println("要素geometry类型名称：" + name);
            System.out.println("shp文件使用的坐标参考系：\n" + coordinateReferenceSystem);

        }

    }

    public static void SHP_DataStructureParse(String shpFileLocation){


        try {
            ShapefileDataStore shapefileDataStore = new ShapefileDataStore(new File(shpFileLocation).toURI().toURL());


            SimpleFeatureType simpleFeatureType = shapefileDataStore.getSchema();

            System.out.println(simpleFeatureType.getCoordinateReferenceSystem().getName());
            System.out.println(simpleFeatureType.getCoordinateReferenceSystem().getCoordinateSystem().getRemarks());

            System.out.println(simpleFeatureType.getCoordinateReferenceSystem().getCoordinateSystem().getAlias());
            System.out.println(simpleFeatureType.getCoordinateReferenceSystem().getIdentifiers());

            //System.out.println("--------------------------");
            //System.out.println(CRS.decode("EPSG:4326 - WGS 84 - Geographic").getName());
            System.out.println(CRS.lookupEpsgCode(simpleFeatureType.getCoordinateReferenceSystem(),true));



            System.out.println("--------------------------");
            List<AttributeType> attributeTypeList = simpleFeatureType.getTypes();
            for(AttributeType currentAttributeType:attributeTypeList){
                System.out.println(currentAttributeType.getName());



            }












            System.out.println(shapefileDataStore.getSchema());
            System.out.println(shapefileDataStore.getCharset());
                    System.out.println(shapefileDataStore.getInfo());



            FeatureCollection featureCollection = shapefileDataStore.getFeatureSource().getFeatures();

            SimpleFeatureIterator features = (SimpleFeatureIterator) featureCollection.features();
            while (features.hasNext()) {
                SimpleFeature next = features.next();


                //next.getFeatureType().getCoordinateReferenceSystem()

                //System.out.println(next.getProperties());
                //System.out.println(next.getAttributes());



                //坐标系转换
                Geometry geometry = (Geometry) next.getDefaultGeometry();
                // Point MultiPoint Polygon MutiPolygon LineString  MultiLineString
                //return geometry.getGeometryType();
                //System.out.println(geometry.getGeometryType());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }






    }
}
