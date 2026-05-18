import { useContext, useRef, useState, useEffect } from "react";
import { LocationContext, ALL_LOCATIONS } from "../context/LocationContext";

function PinIcon() {
  return (
    <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
      <path
        fill="#ff4d4d"
        d="M12 2a7 7 0 0 0-7 7c0 5.25 7 13 7 13s7-7.75 7-13a7 7 0 0 0-7-7zm0 9.5A2.5 2.5 0 1 1 12 6.5a2.5 2.5 0 0 1 0 5z"
      />
    </svg>
  );
}

function ChevronIcon() {
  return (
    <svg viewBox="0 0 24 24" width="14" height="14" aria-hidden="true">
      <path fill="#555" d="M7 10l5 5 5-5z" />
    </svg>
  );
}

function SearchIcon() {
  return (
    <svg viewBox="0 0 24 24" width="18" height="18" aria-hidden="true">
      <path
        fill="#888"
        d="M15.5 14h-.79l-.28-.27A6.471 6.471 0 0 0 16 9.5 6.5 6.5 0 1 0 9.5 16c1.61 0 3.09-.59 4.23-1.57l.27.28v.79l5 4.99L20.49 19l-4.99-5zm-6 0A4.5 4.5 0 1 1 14 9.5 4.5 4.5 0 0 1 9.5 14z"
      />
    </svg>
  );
}

function LocationSearchBar({ searchValue, onSearchChange, placeholder }) {
  const { locations, selectedLocationId, selectedLocation, setSelectedLocationId } =
    useContext(LocationContext);
  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  useEffect(() => {
    const handleClick = (e) => {
      if (dropdownRef.current && !dropdownRef.current.contains(e.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, []);

  const displayLabel =
    selectedLocationId === ALL_LOCATIONS
      ? "All Locations"
      : selectedLocation
      ? selectedLocation.label
      : "Select location";

  const choose = (id) => {
    setSelectedLocationId(id);
    setOpen(false);
  };

  return (
    <div className="location-search-bar">
      <div
        className="lsb-location"
        ref={dropdownRef}
        onClick={() => setOpen((o) => !o)}
        role="button"
        tabIndex={0}
        onKeyDown={(e) => {
          if (e.key === "Enter" || e.key === " ") {
            e.preventDefault();
            setOpen((o) => !o);
          }
        }}
      >
        <span className="lsb-pin-icon">
          <PinIcon />
        </span>
        <span className="lsb-location-text" title={displayLabel}>
          {displayLabel}
        </span>
        <span className="lsb-chevron">
          <ChevronIcon />
        </span>

        {open && (
          <div className="lsb-dropdown" onClick={(e) => e.stopPropagation()}>
            <div
              className={`lsb-dropdown-item ${
                selectedLocationId === ALL_LOCATIONS ? "active" : ""
              }`}
              onClick={() => choose(ALL_LOCATIONS)}
            >
              <span className="lsb-dropdown-name">All Locations</span>
              <span className="lsb-dropdown-sub">Show every restaurant</span>
            </div>
            {locations.map((l) => (
              <div
                key={l.id}
                className={`lsb-dropdown-item ${
                  String(selectedLocationId) === String(l.id) ? "active" : ""
                }`}
                onClick={() => choose(l.id)}
              >
                <span className="lsb-dropdown-name">{l.name}</span>
                <span className="lsb-dropdown-sub">{l.label}</span>
              </div>
            ))}
            {locations.length === 0 && (
              <div className="lsb-dropdown-empty">No locations available</div>
            )}
          </div>
        )}
      </div>

      <div className="lsb-divider" />

      <div className="lsb-search">
        <span className="lsb-search-icon">
          <SearchIcon />
        </span>
        <input
          className="lsb-search-input"
          type="text"
          value={searchValue}
          onChange={(e) => onSearchChange(e.target.value)}
          placeholder={placeholder || "Search for restaurant, cuisine or a dish"}
        />
      </div>
    </div>
  );
}

export default LocationSearchBar;
