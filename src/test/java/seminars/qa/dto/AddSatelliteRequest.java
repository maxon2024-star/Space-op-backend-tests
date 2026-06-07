package seminars.qa.dto;

import java.util.Map;

public record AddSatelliteRequest(String constellationName, Map<String, Object> satelliteParam) {}