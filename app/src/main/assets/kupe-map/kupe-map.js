var params = new URLSearchParams(location.search);
var CFG = {
  style: params.get('style'),
  lat: parseFloat(params.get('lat')),
  lng: parseFloat(params.get('lng')),
  zoom: parseFloat(params.get('zoom')),
  lang: params.get('lang') || 'en',
  debug: params.get('debug') === '1'
};
var dbg = document.getElementById('dbg');

if (CFG.debug) document.body.classList.add('debug');

function log(t) {
  dbg.textContent += '\n' + t;
  dbg.scrollTop = dbg.scrollHeight;
  try { Android.log(t); } catch (e) {}
}
window.onerror = function (m) { log('ERROR: ' + m); };

var starsEl = document.getElementById('stars');

function mulberry32(a) {
  return function () {
    a |= 0; a = (a + 0x6D2B79F5) | 0;
    var t = Math.imul(a ^ (a >>> 15), 1 | a);
    t = (t + Math.imul(t ^ (t >>> 7), 61 | t)) ^ t;
    return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  };
}

function drawStars() {
  var w = window.innerWidth, h = window.innerHeight;
  var dpr = Math.min(window.devicePixelRatio || 1, 2);
  starsEl.width = w * dpr;
  starsEl.height = h * dpr;
  starsEl.style.width = w + 'px';
  starsEl.style.height = h + 'px';
  var ctx = starsEl.getContext('2d');
  ctx.scale(dpr, dpr);

  var g = ctx.createRadialGradient(w / 2, h / 2, 0, w / 2, h / 2, Math.max(w, h) * 0.75);
  g.addColorStop(0, '#0b1230');
  g.addColorStop(1, '#03040a');
  ctx.fillStyle = g;
  ctx.fillRect(0, 0, w, h);

  var rnd = mulberry32(20260101);
  var tints = ['255,255,255', '200,215,255', '255,240,215'];
  for (var i = 0; i < 260; i++) {
    var x = rnd() * w, y = rnd() * h;
    var big = rnd();
    var r = big > 0.97 ? 1.5 : big > 0.85 ? 1.0 : 0.6;
    var a = 0.35 + rnd() * 0.65;
    var tint = tints[Math.floor(rnd() * tints.length)];
    ctx.beginPath();
    ctx.arc(x, y, r, 0, Math.PI * 2);
    ctx.fillStyle = 'rgba(' + tint + ',' + a.toFixed(2) + ')';
    ctx.fill();
  }
}

var mapEl = document.getElementById('mapContainer');

function fitMap() {
  mapEl.style.width = window.innerWidth + 'px';
  mapEl.style.height = window.innerHeight + 'px';
  if (window.map && window.map.resize) window.map.resize();
}

function fitAll() { drawStars(); fitMap(); }

fitAll();
window.addEventListener('resize', fitAll);

window.applyLanguage = function (lang) {
  var expr = ['coalesce', ['get', 'name:' + lang], ['get', 'name']];
  var changed = 0;
  map.getStyle().layers.forEach(function (l) {
    if (l.type !== 'symbol' || !l.layout || !l.layout['text-field']) return;
    var s = JSON.stringify(l.layout['text-field']);
    if (s.indexOf('name') < 0) return;
    if (/\bele\b/.test(s)) return;
    map.setLayoutProperty(l.id, 'text-field', expr);
    changed++;
  });
};

try {
  window.map = new maplibregl.Map({
    container: 'mapContainer',
    style: CFG.style,
    center: [CFG.lng, CFG.lat],
    zoom: CFG.zoom,
    attributionControl: { compact: true }
  });

  map.on('style.load', function () {
    map.setProjection({ type: 'globe' });
    window.applyLanguage(CFG.lang);
  });

  map.on('load', function () {
    fitMap();
    Android.onMapReady();
  });

  map.on('error', function (e) {
    log('map error: ' + (e.error ? e.error.message : JSON.stringify(e)));
  });
} catch (e) {
  log('EXCEPCION: ' + e.message);
}