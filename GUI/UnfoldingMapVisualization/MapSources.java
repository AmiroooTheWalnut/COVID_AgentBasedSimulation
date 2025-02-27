/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package COVID_AgentBasedSimulation.GUI.UnfoldingMapVisualization;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.providers.AbstractMapProvider;
import de.fhpotsdam.unfolding.providers.EsriProvider;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.Microsoft;
import de.fhpotsdam.unfolding.providers.OpenStreetMap;
import de.fhpotsdam.unfolding.providers.StamenMapProvider;
import de.fhpotsdam.unfolding.providers.ThunderforestProvider;
import java.util.ArrayList;
import processing.core.PApplet;

/**
 *
 * @author Amir Mohammad Esmaieeli Sikaroudi
 */
public class MapSources {

    public ArrayList<MapSourse> maps = new ArrayList<>();

    public MapSources(PApplet parent, int width, int height) {
        try {
            UnfoldingMap microsoftMapRoads = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new Microsoft.RoadProvider());
            this.maps.add(new MapSourse(microsoftMapRoads, "MicrosoftRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftRoads_FAILED"));
        }
        try {
            UnfoldingMap microsoftMapTerrain = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new Microsoft.AerialProvider());
            this.maps.add(new MapSourse(microsoftMapTerrain, "MicrosoftTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftTerrain_FAILED"));
        }
        try {
            UnfoldingMap openStreetMap = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new OpenStreetMap.OpenStreetMapProvider());
            this.maps.add(new MapSourse(openStreetMap, "OpenStreetMap"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "OpenStreetMap_FAILED"));
        }
        try {
            UnfoldingMap esriMapRoads = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.WorldStreetMap());
            this.maps.add(new MapSourse(esriMapRoads, "EsriRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriRoads_FAILED"));
        }
        try {
            UnfoldingMap esriMapNationalGeography = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.NatGeoWorldMap());
            this.maps.add(new MapSourse(esriMapNationalGeography, "EsriNationalGeography"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriNationalGeography_FAILED"));
        }
        try {
            UnfoldingMap esriMapGeneric = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.WorldTopoMap());
            this.maps.add(new MapSourse(esriMapGeneric, "EsriGeneric"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGeneric_FAILED"));
        }
        try {
            UnfoldingMap esriMapGenericGray = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.WorldGrayCanvas());
            this.maps.add(new MapSourse(esriMapGenericGray, "EsriGenericGray"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGenericGray_FAILED"));
        }
        try {
            UnfoldingMap esriMapWorld = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.WorldShadedRelief());
            this.maps.add(new MapSourse(esriMapWorld, "EsriWorld"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriWorld_FAILED"));
        }
        try {
            UnfoldingMap esriMapElevation = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new EsriProvider.WorldTerrain());
            this.maps.add(new MapSourse(esriMapElevation, "EsriElevation"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriElevation_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapLandscape = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new ThunderforestProvider.Landscape());
            this.maps.add(new MapSourse(thunderForestMapLandscape, "ThunderForestLandscape"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestLandscape_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOutdoors = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new ThunderforestProvider.Outdoors());
            this.maps.add(new MapSourse(thunderForestMapOutdoors, "ThunderForestOutdoors"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOutdoors_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOpenCycle = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new ThunderforestProvider.OpenCycleMap());
            this.maps.add(new MapSourse(thunderForestMapOpenCycle, "ThunderForestOpenCycle"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOpenCycle_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapTransport = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new ThunderforestProvider.Transport());
            this.maps.add(new MapSourse(thunderForestMapTransport, "ThunderForestTransport"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestTransport_FAILED"));
        }
        try {
            UnfoldingMap stamenMapBlackWhite = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new StamenMapProvider.TonerBackground());
            this.maps.add(new MapSourse(stamenMapBlackWhite, "StamenMapBlackWhite"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWhite_FAILED"));
        }
        try {
            UnfoldingMap stamenMapWater = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new StamenMapProvider.WaterColor());
            this.maps.add(new MapSourse(stamenMapWater, "StamenMapBlackWater"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWater_FAILED"));
        }
        try {
            UnfoldingMap googleMapRoads = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new Google.GoogleMapProvider());
            this.maps.add(new MapSourse(googleMapRoads, "GoogleMapRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapRoads_FAILED"));
        }
        try {
            UnfoldingMap googleMapTerrain = new UnfoldingMap(parent, 0, 0, width, height, (AbstractMapProvider) new Google.GoogleTerrainProvider());
            this.maps.add(new MapSourse(googleMapTerrain, "GoogleMapTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapTerrain_FAILED"));
        }
    }
    
    public MapSources(PApplet parent) {
        try {
            UnfoldingMap microsoftMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new Microsoft.RoadProvider());
            this.maps.add(new MapSourse(microsoftMapRoads, "MicrosoftRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftRoads_FAILED"));
        }
        try {
            UnfoldingMap microsoftMapTerrain = new UnfoldingMap(parent, (AbstractMapProvider) new Microsoft.AerialProvider());
            this.maps.add(new MapSourse(microsoftMapTerrain, "MicrosoftTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftTerrain_FAILED"));
        }
        try {
            UnfoldingMap openStreetMap = new UnfoldingMap(parent, (AbstractMapProvider) new OpenStreetMap.OpenStreetMapProvider());
            this.maps.add(new MapSourse(openStreetMap, "OpenStreetMap"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "OpenStreetMap_FAILED"));
        }
        try {
            UnfoldingMap esriMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldStreetMap());
            this.maps.add(new MapSourse(esriMapRoads, "EsriRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriRoads_FAILED"));
        }
        try {
            UnfoldingMap esriMapNationalGeography = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.NatGeoWorldMap());
            this.maps.add(new MapSourse(esriMapNationalGeography, "EsriNationalGeography"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriNationalGeography_FAILED"));
        }
        try {
            UnfoldingMap esriMapGeneric = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldTopoMap());
            this.maps.add(new MapSourse(esriMapGeneric, "EsriGeneric"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGeneric_FAILED"));
        }
        try {
            UnfoldingMap esriMapGenericGray = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldGrayCanvas());
            this.maps.add(new MapSourse(esriMapGenericGray, "EsriGenericGray"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGenericGray_FAILED"));
        }
        try {
            UnfoldingMap esriMapWorld = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldShadedRelief());
            this.maps.add(new MapSourse(esriMapWorld, "EsriWorld"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriWorld_FAILED"));
        }
        try {
            UnfoldingMap esriMapElevation = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldTerrain());
            this.maps.add(new MapSourse(esriMapElevation, "EsriElevation"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriElevation_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapLandscape = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Landscape());
            this.maps.add(new MapSourse(thunderForestMapLandscape, "ThunderForestLandscape"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestLandscape_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOutdoors = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Outdoors());
            this.maps.add(new MapSourse(thunderForestMapOutdoors, "ThunderForestOutdoors"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOutdoors_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOpenCycle = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.OpenCycleMap());
            this.maps.add(new MapSourse(thunderForestMapOpenCycle, "ThunderForestOpenCycle"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOpenCycle_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapTransport = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Transport());
            this.maps.add(new MapSourse(thunderForestMapTransport, "ThunderForestTransport"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestTransport_FAILED"));
        }
        try {
            UnfoldingMap stamenMapBlackWhite = new UnfoldingMap(parent, (AbstractMapProvider) new StamenMapProvider.TonerBackground());
            this.maps.add(new MapSourse(stamenMapBlackWhite, "StamenMapBlackWhite"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWhite_FAILED"));
        }
        try {
            UnfoldingMap stamenMapWater = new UnfoldingMap(parent, (AbstractMapProvider) new StamenMapProvider.WaterColor());
            this.maps.add(new MapSourse(stamenMapWater, "StamenMapBlackWater"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWater_FAILED"));
        }
        try {
            UnfoldingMap googleMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new Google.GoogleMapProvider());
            this.maps.add(new MapSourse(googleMapRoads, "GoogleMapRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapRoads_FAILED"));
        }
        try {
            UnfoldingMap googleMapTerrain = new UnfoldingMap(parent, (AbstractMapProvider) new Google.GoogleTerrainProvider());
            this.maps.add(new MapSourse(googleMapTerrain, "GoogleMapTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapTerrain_FAILED"));
        }
    }

    public MapSources(COVIDGeoVisualization parent) {
        try {
            UnfoldingMap microsoftMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new Microsoft.RoadProvider());
            this.maps.add(new MapSourse(microsoftMapRoads, "MicrosoftRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftRoads_FAILED"));
        }
        try {
            UnfoldingMap microsoftMapTerrain = new UnfoldingMap(parent, (AbstractMapProvider) new Microsoft.AerialProvider());
            this.maps.add(new MapSourse(microsoftMapTerrain, "MicrosoftTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "MicrosoftTerrain_FAILED"));
        }
        try {
            UnfoldingMap openStreetMap = new UnfoldingMap(parent, (AbstractMapProvider) new OpenStreetMap.OpenStreetMapProvider());
            this.maps.add(new MapSourse(openStreetMap, "OpenStreetMap"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "OpenStreetMap_FAILED"));
        }
        try {
            UnfoldingMap esriMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldStreetMap());
            this.maps.add(new MapSourse(esriMapRoads, "EsriRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriRoads_FAILED"));
        }
        try {
            UnfoldingMap esriMapNationalGeography = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.NatGeoWorldMap());
            this.maps.add(new MapSourse(esriMapNationalGeography, "EsriNationalGeography"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriNationalGeography_FAILED"));
        }
        try {
            UnfoldingMap esriMapGeneric = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldTopoMap());
            this.maps.add(new MapSourse(esriMapGeneric, "EsriGeneric"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGeneric_FAILED"));
        }
        try {
            UnfoldingMap esriMapGenericGray = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldGrayCanvas());
            this.maps.add(new MapSourse(esriMapGenericGray, "EsriGenericGray"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriGenericGray_FAILED"));
        }
        try {
            UnfoldingMap esriMapWorld = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldShadedRelief());
            this.maps.add(new MapSourse(esriMapWorld, "EsriWorld"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriWorld_FAILED"));
        }
        try {
            UnfoldingMap esriMapElevation = new UnfoldingMap(parent, (AbstractMapProvider) new EsriProvider.WorldTerrain());
            this.maps.add(new MapSourse(esriMapElevation, "EsriElevation"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "EsriElevation_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapLandscape = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Landscape());
            this.maps.add(new MapSourse(thunderForestMapLandscape, "ThunderForestLandscape"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestLandscape_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOutdoors = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Outdoors());
            this.maps.add(new MapSourse(thunderForestMapOutdoors, "ThunderForestOutdoors"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOutdoors_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapOpenCycle = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.OpenCycleMap());
            this.maps.add(new MapSourse(thunderForestMapOpenCycle, "ThunderForestOpenCycle"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestOpenCycle_FAILED"));
        }
        try {
            UnfoldingMap thunderForestMapTransport = new UnfoldingMap(parent, (AbstractMapProvider) new ThunderforestProvider.Transport());
            this.maps.add(new MapSourse(thunderForestMapTransport, "ThunderForestTransport"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "ThunderForestTransport_FAILED"));
        }
        try {
            UnfoldingMap stamenMapBlackWhite = new UnfoldingMap(parent, (AbstractMapProvider) new StamenMapProvider.TonerBackground());
            this.maps.add(new MapSourse(stamenMapBlackWhite, "StamenMapBlackWhite"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWhite_FAILED"));
        }
        try {
            UnfoldingMap stamenMapWater = new UnfoldingMap(parent, (AbstractMapProvider) new StamenMapProvider.WaterColor());
            this.maps.add(new MapSourse(stamenMapWater, "StamenMapBlackWater"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "StamenMapBlackWater_FAILED"));
        }
        try {
            UnfoldingMap googleMapRoads = new UnfoldingMap(parent, (AbstractMapProvider) new Google.GoogleMapProvider());
            this.maps.add(new MapSourse(googleMapRoads, "GoogleMapRoads"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapRoads_FAILED"));
        }
        try {
            UnfoldingMap googleMapTerrain = new UnfoldingMap(parent, (AbstractMapProvider) new Google.GoogleTerrainProvider());
            this.maps.add(new MapSourse(googleMapTerrain, "GoogleMapTerrain"));
        } catch (Exception ex) {
            this.maps.add(new MapSourse(null, "GoogleMapTerrain_FAILED"));
        }
    }
}
