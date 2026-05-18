import { createContext, useEffect, useState, useCallback } from "react";
import API from "../services/api";

export const LocationContext = createContext();

const STORAGE_KEY = "selectedLocationId";
export const ALL_LOCATIONS = "ALL";

function loadSelected() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    return raw || ALL_LOCATIONS;
  } catch {
    return ALL_LOCATIONS;
  }
}

export function LocationProvider({ children }) {
  const [locations, setLocations] = useState([]);
  const [selectedLocationId, setSelectedLocationIdState] = useState(loadSelected);
  const [loaded, setLoaded] = useState(false);

  const fetchLocations = useCallback(() => {
    return API.get("/locations")
      .then((res) => setLocations(res.data || []))
      .catch((err) => console.error("Error fetching locations:", err))
      .finally(() => setLoaded(true));
  }, []);

  useEffect(() => {
    const token = localStorage.getItem("token");
    if (token) {
      fetchLocations();
    } else {
      setLoaded(true);
    }
  }, [fetchLocations]);

  const setSelectedLocationId = (id) => {
    const value = id == null ? ALL_LOCATIONS : String(id);
    localStorage.setItem(STORAGE_KEY, value);
    setSelectedLocationIdState(value);
  };

  const selectedLocation =
    selectedLocationId === ALL_LOCATIONS
      ? null
      : locations.find((l) => String(l.id) === String(selectedLocationId)) || null;

  return (
    <LocationContext.Provider
      value={{
        locations,
        loaded,
        selectedLocationId,
        selectedLocation,
        setSelectedLocationId,
        refreshLocations: fetchLocations,
      }}
    >
      {children}
    </LocationContext.Provider>
  );
}
