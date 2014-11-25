'use strict';
/* -----GOOGLE MODULE BEGIN-----*/
var GMaper = function(h) {
    var that = this, // [f]
        mapObj = null, // [l]
        directionsService = new google.maps.DirectionsService(), // [m]
        directionsRenderer = null, // [u]
        markers = {}, // [s]
        infoWindows = {}, // [z]
        createMarker = null, // [x]  add marker
        createInfoWindow = null; // [y]  create InfoWindow
    that.init = function(p) {
        /* p为以下对象
            {   // 提供id和中心点
                mapInner: "dayMapInner",
                lng: h[0][0].lng, // 第一天的第一个
                lat: h[0][0].lat
            }
        */
        var mapDiv = document.getElementById(p.mapInner),
            q = true;
        false === h.scrollWheel && (q = false);
        var s = h.mapTypeControl || false;
        mapDiv && (mapObj = new google.maps.Map(mapDiv, {
            mapTypeControl: s,
            center: new google.maps.LatLng(p.lat, p.lng),
            zoom: 12,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            scrollwheel: q,
            // 调节比例尺位置到右边
            zoomControlOptions: {
                position: google.maps.ControlPosition.TOP_RIGHT,
                style: google.maps.ZoomControlStyle.LARGE
            },
            // 去掉导航圆盘
            panControl: false, //去掉
            // 不要街景小人icon
            streetViewControl: false,
        }));
        directionsRenderer = new google.maps.DirectionsRenderer({
            suppressMarkers: true,
            preserveViewport: true
        });
        google.maps.event.addListener(mapObj, "click", function(l) {
            h.clickListener && "function" == $.type(h.clickListener) &&
                h.clickListener();
            that.hideInfoWindows();
        });
        google.maps.event.addListener(mapObj, "idle", function(f) {
            h.idleListener && "function" == $.type(h.idleListener) &&
                h.idleListener(this.getCenter(), this.getZoom());
        });
    };
    that.getMap = function() {
        return mapObj;
    }
    that.setCenterByLatLng = function(lat, lng) {
        mapObj.setCenter(new google.maps.LatLng(lat, lng));
    };
    that.setCenter = function(latLng) {
        mapObj.setCenter(latLng);
    };
    that.getCenter = function() {
        return mapObj.getCenter();
    };
    that.setZoom = function(num) {
        mapObj.setZoom(num);
    };
    that.getZoom = function() {
        return mapObj.getZoom();
    };
    that.clearRoute = function() {
        directionsRenderer.setMap(null);
    };
    that.clearMap = function() {
        directionsRenderer.setMap(null);
        that.clearInfoWindow();
        for (var h in markers) markers.hasOwnProperty(h) && markers[h].setMap(
            null);
        markers = {};
    };
    that.setFitView = function() {
        var bound = new google.maps.LatLngBounds(),
            h;
        for (h in markers)
            // filter markers show on the map
            markers[h].getMap() && bound.extend(markers[h].latLng);
        mapObj.fitBounds(bound);
    };
    that.addMarkers = function(markers) {
        for (var l = markers.length, m = 0; m < l; m++) that.addMarker(
            markers[m], function() {});
    };
    //////////////////////////////////////////////////////////////////
    that.addMarker = function(f, callback) {
        var latLng = new google.maps.LatLng(f.lat, f.lng),
            id = f.id;
            if (!that.checkMarker(id)) {
                var marker = new createMarker(f.markerHtml, latLng, function() {
                    var f = $(this.div.firstChild);
                    callback(f, latLng);
                });
                marker.setMap(mapObj);
                markers[id] = marker;
            }else{
                markers[id].setMap(mapObj);
            }
    };
    that.getMarker = function(id) {
        var marker;
        //markers[id] && marker = markers[id]
        return marker;
    };
    that.removeMarker = function(id) {
        var h = markers[id];
        h && (h.setMap(null), delete markers[id]);
    };
    that.removeMarkers = function(markers) {
        for (var l = markers.length, m = 0; m < l; m++) that.removeMarker(
            markers[m]);
    };
    that.showMarker = function(f) {
        markers[f] && markers[f].setMap(mapObj);
    };
    that.showMarkers = function(markers) {
        for (var l = markers.length, m = 0; m < l; m++) that.showMarker(
            markers[m]);
    };
    that.hideMarker = function(id) {
        markers[id] && markers[id].setMap(null);
    };
    that.hideMarkers = function(markers) {
        for (var l = markers.length, m = 0; m < l; m++)
            that.hideMarker(markers[m]);
    };
    that.checkMarker = function(id) {
        return markers[id] ? true : false;
    };
    that.checkMarkerShow = function(id) {
        return markers[id] && markers[id].getMap() ? true : false;
    };
    //------------------------------------
    that.createInfo = function(data, callback) {//h:data
        console.log('data is:');
        console.log(data);
        var id = data.id,
            infoWindow = infoWindows[id];
        console.log(id);
        if (infoWindow) {
            if (infoWindow.getMap()) return;
        } else {
            console.log('console out createInfoWindow\'s first input arguement');
            console.log(data.infoHtml);
            infoWindow = new createInfoWindow(data.infoHtml, data.latLng, data.gOffset || [
            0, -103], function() {
                console.dir($(this.div.firstChild));
                callback($(this.div.firstChild));
            }),
            infoWindows[id] = infoWindow;
        }
        that.hideInfoWindows();
        infoWindow.setMap(mapObj);
    };
    that.checkInfo = function(id) {
        return infoWindows[id] ? true : false;
    };
    that.getInfo = function(id) {
        return infoWindows[id] ? infoWindows[id] : null;
    };
    that.openInfo = function(id) {
        that.hideInfoWindows();
        that.getInfo(id)
            .setMap(mapObj);
    };
    that.hideInfoWindow = function(id) {
        infoWindows[id] && infoWindows[id].setMap(null);
    };
    that.hideInfoWindows = function() {
        for (var f in infoWindows) infoWindows.hasOwnProperty(f) &&
            infoWindows[f].setMap(null);
    };
    that.clearInfoWindow = function() {
        for (var f in infoWindows) infoWindows.hasOwnProperty(f) &&
            infoWindows[f].setMap(null);
        infoWindows = {};
    };
    that.drawRoute = function(f, mode, callback) {
        for (var s = f.length - 1,
            x = 1,
            waypoints = [],
            start = new google.maps.LatLng(f[0].lat, f[0].lng),
            end = new google.maps.LatLng(f[s].lat, f[s].lng); x < s; x++) {
            var C = new google.maps.LatLng(f[x].lat, f[x].lng);
            waypoints.push({
                location: C,
                stopover: !0
            });
        }
        directionsService.route({
            origin: start,
            destination: end,
            waypoints: waypoints,
            optimizeWaypoints: !1,
            travelMode: mode,
            unitSystem: google.maps.UnitSystem.METRIC
        }, function(result, state) {
            var m = {
                msg: 0
            };
            if (state == google.maps.DirectionsStatus.OK && (
                directionsRenderer.getMap() ||
                directionsRenderer.setMap(mapObj),
                directionsRenderer.setDirections(result),
                result.routes && result.routes[0].legs && 0 <
                result.routes[0].legs.length)) {
                m.msg = 1;
                var v = result.routes[0].legs[0],
                    p = v.steps,
                    t = p.length,
                    s = 0;
                m.distance = v.distance.text;
                m.time = v.duration.text;
                for (m.steps = []; s < t; s++) v = p[s].instructions,
                    v = v.replace(/<\/?[^>]*>/g, ""),
                    m.steps.push(v);
            }
            callback(m);
        });
    };
    that.drawWalkRoute = function(h, l) {
        that.drawRoute(h, google.maps.TravelMode.WALKING, l);
    };
    that.drawDriveRoute = function(h, l) {
        that.drawRoute(h, google.maps.TravelMode.DRIVING, l);
    };
    that.drawPublicTransitRoute = function(h, l) {
        that.drawRoute(h, google.maps.TravelMode.TRANSIT, l);
    };


    /*----create marker on the map zone BEGIN----*/
    (function() {
        createMarker = function(html, latLng, callback) {
            this.html = html;
            this.latLng = latLng;
            this.ready = callback;
            this.div = null;
        };
        createMarker.prototype = new google.maps.OverlayView();
        $.extend(createMarker.prototype, {
            onAdd: function() {
                var f = document.createElement("div");
                f.innerHTML = this.html;
                f.style.position = "absolute";
                this.getPanes().overlayImage.appendChild(f);
                this.div = f;
                this.ready.call(this);
            },
            draw: function() {
                this.dom = this.div.firstChild;
                var f = this.getProjection().fromLatLngToDivPixel(this.latLng);
                this.div.style.left = f.x - 15 + "px";
                this.div.style.top = f.y - 40 + "px";
            },
            onRemove: function() {
                this.div.parentNode.removeChild(this.div);
                this.div = null;
            }
        });
    })();
    /*----create marker on the map zone END----*/


    /*----create info window on the map zone BEGIN----*/
    (function() {
        createInfoWindow = function(html, latLng, offset, callback) {
            this.html = html;
            this.latLng = latLng;
            this.ready = callback;
            this.div = null;
            this.offset = offset;
        };
        //map.setCenter(latLng);
        //map.setZoom(10);
        // console.log(mapObj);
        // console.log(mapObj.getCenter());
        // console.log(mapObj.getZoom());
        createInfoWindow.prototype = new google.maps.OverlayView();
        $.extend(createInfoWindow.prototype, {
            onAdd: function() {
                var divDom = document.createElement("div");
                divDom.innerHTML = this.html;
                divDom.style.position = "absolute";
                divDom.style.zIndex = "5";
                this.getPanes().overlayImage.appendChild(divDom);
                this.div = divDom;
                this.ready.call(this);
            },
            draw: function() {
                // "this" is global function
                //获取屏幕像素坐标和经纬度对之间的转换
                console.log('bound......');
                console.log(mapObj);
                console.log(mapObj.getCenter());
                console.log(mapObj.getZoom());
                var coordinate_pixel = this.getProjection(),
                    mapBound = mapObj.getBounds();
                     console.log(mapBound);
                    var divPixel = coordinate_pixel.fromLatLngToDivPixel(this.latLng),
                    // s = mapBound.getNorthEast(),
                    // u = mapBound.getSouthWest(),
                    // x = coordinate_pixel.fromLatLngToDivPixel(mapBound.getNorthEast()),
                    // f = coordinate_pixel.fromLatLngToDivPixel(mapBound.getSouthWest()),
                    v = $(this.div.firstChild).outerWidth(),
                    h = $(this.div.firstChild).outerHeight(),
                    v = v / 2,
                    left = divPixel.x - v + this.offset[0] + 50 ,
                    bottom = -divPixel.y + (h + this.offset[1]) - 200;
                    console.log($(this.div.firstChild));
                    console.log(v);
                    console.log(h);
                this.div.style.left = left + "px";
                this.div.style.bottom = bottom + "px";
                // console.log('bound......');
                // console.log(mapBound);
                // var t = 0,
                //     C = 0;
                // m.x < f.x + v + 5 && (t = m.x - (f.x + v + 5));
                // m.x > x.x - v - 5 && (t = m.x - (x.x - v - 5));
                // C = m.y - (h + 55) - x.y;
                // C = 0 > C ? C : 0;
                // 180 !== s && -180 !== u ? (t || C) &&
                // mapObj.panBy(t, C) : C && mapObj.panBy(0, C);
            },
            onRemove: function() {
                this.div.parentNode.removeChild(this.div);
                this.div = null;
            }
        });
    })();
};
    /*----create info window on the map zone END----*/

/*-----GOOGLE MODULE END-----*/