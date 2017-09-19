function _classCallCheck(instance, Constructor) { if (!(instance instanceof Constructor)) { throw new TypeError("Cannot call a class as a function"); } }

// Based on the stem generator in Flash Math Creativity

var EventManager = function () {
  function EventManager() {
    _classCallCheck(this, EventManager);

    this.eventLookup = {};
  }

  EventManager.prototype.off = function off(event, callback) {
    var listeners = this.eventLookup[event];
    if (event === "*") this.eventLookup = {};else if (!callback) this.eventLookup[event] = [];else _.remove(listeners, { callback: callback });
  };

  EventManager.prototype.on = function on(event, callback, scope) {
    var listeners = this.eventLookup[event];
    if (!listeners) this.eventLookup[event] = listeners = [];
    listeners.push({ callback: callback, scope: scope });
    return function () {
      return _.remove(listeners, { callback: callback });
    };
  };

  EventManager.prototype.once = function once(event, callback, scope) {
    var _this = this;

    var on = function on() {
      for (var _len = arguments.length, data = Array(_len), _key = 0; _key < _len; _key++) {
        data[_key] = arguments[_key];
      }

      _this.off(event, on);
      callback.apply(scope, data);
    };
    return this.on(event, on);
  };

  EventManager.prototype.fire = function fire(event) {
    for (var _len2 = arguments.length, data = Array(_len2 > 1 ? _len2 - 1 : 0), _key2 = 1; _key2 < _len2; _key2++) {
      data[_key2 - 1] = arguments[_key2];
    }

    var listeners = this.eventLookup[event];
    if (!listeners) return;
    listeners.forEach(function (list) {
      try {
        return list.callback.apply(list.scope, data);
      } catch (e) {
        return _.isError(e) ? e : new Error(e);
      }
    });
  };

  return EventManager;
}();

var events = new EventManager();

var ns = "http://www.w3.org/2000/svg";
var d = "M0,0 Q5,-5 10,0 5,5 0,0z";

var stems = $("#stems");
var leaves = $("#leaves");
var svg = $("svg");

var leafCount = 30;
var plants = 28;
var centerX = 480;
var offsetX = 480;

$("#create").on("click", generate);

generate();

function generate() {

  leaves.empty();
  stems.empty();

  _.times(plants, createPlant);

  stems.children().each(function () {
    var _this2 = this;

    var tween = TweenMax.to(this, _.random(2, 4, true), {
      drawSVG: true,
      delay: _.random(2, true),
      onStart: function onStart() {
        return TweenLite.set(_this2, { opacity: 1 });
      },
      onUpdate: function onUpdate() {
        return events.fire(_this2.id, tween.progress());
      }
    });
  });
}

function createPlant() {

  var points = createPoints();
  var stem = createPath(stems);
  var length = points.length;
  var values = points.map(function (point) {
    return point.x + "," + point.y;
  });
  var height = points[length - 1].y;
  var id = _.uniqueId("grow");
  var coords = [];

  for (var i = 0; i < length; i++) {
    var point = points[i];
    coords.push(point.x, point.y);
  }

  TweenLite.set(stem, {
    opacity: 0,
    //attr: { id, d: `M${values.join(" ")}` }
    attr: { id: id, d: solve(coords) }
  });

  for (var i = 0; i < leafCount; i++) {
    var point = points[length - 1 - i];
    var scale = {
      x: 1 + 0.1 * i,
      y: 1 + 0.05 * i
    };
    createLeaf(point, scale, height, id);
  }
}

function createLeaf(point, scale, height, grow) {

  var leaf = createPath(leaves);
  var start = height / point.y;
  var off = events.on(grow, growLeaf);

  function growLeaf(growth) {

    if (growth >= start) {

      // Remove listener
      off();

      TweenLite.set(leaf, {
        x: point.x,
        y: point.y,
        scaleX: scale.x,
        scaleY: scale.y,
        rotation: _.random(180) - 180,
        fill: "rgb(0," + _.random(80, 160) + ",70)",
        attr: { d: d }
      });

      TweenLite.from(leaf, 1, { scale: 0 });
    }
  }
}

function createPoints() {

  var x = _.random(centerX - offsetX, centerX + offsetX);
  var y = 1200;
  var dy = 6;
  var offset = 0.007;
  var count = _.random(30, 55);
  var points = [{ x: x, y: y }];

  for (var i = 1; i <= count; i++) {
    points.push({
      x: points[i - 1].x + i * offset * (_.random(21) - 10),
      y: 795 - dy * i
    });
  }
  return points;
}

function createPath(parent) {
  return $(document.createElementNS(ns, "path")).appendTo(parent);
}

function solve(data) {

  var size = data.length;
  var last = size - 4;

  var path = "M" + [data[0], data[1]];

  for (var i = 0; i < size - 2; i += 2) {

    var x0 = i ? data[i - 2] : data[0];
    var y0 = i ? data[i - 1] : data[1];

    var x1 = data[i + 0];
    var y1 = data[i + 1];

    var x2 = data[i + 2];
    var y2 = data[i + 3];

    var x3 = i !== last ? data[i + 4] : x2;
    var y3 = i !== last ? data[i + 5] : y2;

    var cp1x = (-x0 + 6 * x1 + x2) / 6;
    var cp1y = (-y0 + 6 * y1 + y2) / 6;

    var cp2x = (x1 + 6 * x2 - x3) / 6;
    var cp2y = (y1 + 6 * y2 - y3) / 6;

    path += "C" + [cp1x, cp1y, cp2x, cp2y, x2, y2];
  }

  return path;
}

/* THIS WILL MAKE THE TITLE FADE IN ON FIRSTPAGE */

(function($){
function injector(t, splitter, klass, after) {
var a = t.text().split(splitter), inject = '';

if (a.length) {
$(a).each(function(i, item) {

/* -----------------------------
   randomize array
   ----------------------------- */
var random_index = Math.floor(Math.random()*a.length);

inject += '<span class="'+klass+(random_index+1)+'">'+item+'</span>'+after;
});
t.empty().append(inject);
}
}

var methods = {
init : function() {

return this.each(function() {
injector($(this), '', 'char', '');
});

},

words : function() {

return this.each(function() {
injector($(this), ' ', 'word', ' ');
});

},

lines : function() {

return this.each(function() {
var r = "eefec303079ad17405c889e092e105b0";
// Because it's hard to split a <br/> tag consistently across browsers,
// (*ahem* IE *ahem*), we replaces all <br/> instances with an md5 hash
// (of the word "split"). If you're trying to use this plugin on that
// md5 hash string, it will fail because you're being ridiculous.
injector($(this).children("br").replaceWith(r).end(), r, 'line', '');
});

}
};

$.fn.lettering = function( method ) {
// Method calling logic
if ( method && methods[method] ) {
return methods[ method ].apply( this, [].slice.call( arguments, 1 ));
} else if ( method === 'letters' || ! method ) {
return methods.init.apply( this, [].slice.call( arguments, 0 ) ); // always pass an array
}
$.error( 'Method ' + method + ' does not exist on jQuery.lettering' );
return this;
};

})(jQuery);

$('h1').lettering();

/*  RANDOM MESSAGE ON HEADERS */

const messages = ['grow and show', 'planting a better world', 'keep it in your plants', 'soil meets body', 'wet yo plants', 'remember your roots', 'use with caution', 'not enough thyme', 'drop ya pansies'];
const randomIndex = Math.floor(Math.random()*messages.length);
document.getElementById("randomHeader").innerHTML = messages[randomIndex];

/* HIDE AND SHOW LOGIN FORM */

$(document).ready(function(){
    $("#loginButton").click(function(){
      $("#form").show();
        $("#randomHeader").hide();
    });
});

$(document).mouseup(function (e)
{
    var container = $("#form"); // YOUR CONTAINER SELECTOR

    if (!container.is(e.target) // if the target of the click isn't the container...
        && container.has(e.target).length === 0) // ... nor a descendant of the container
    {
        container.fadeOut();
        $("#randomHeader").fadeIn();

    }
});

/* HIDE AND SHOW LOGIN FORM ON ABOUT */


$(document).ready(function(){
    $("#loginButton").click(function(){
        $("#form").show();
        $("#aboutContent").hide();
    });
});


$(document).mouseup(function (e)
{
    var container = $("#form"); // YOUR CONTAINER SELECTOR

    if (!container.is(e.target) // if the target of the click isn't the container...
        && container.has(e.target).length === 0) // ... nor a descendant of the container
    {
        container.fadeOut();
        $("#aboutContent").fadeIn();
    }
});

/* ADD PLANT BUTTON */

$(document).ready(function(){
    $("#addPlantButton").click(function(){
        $("#addPlantExtended").show();
    });
});

/*document.addEventListener('invalid', (function(){
    return function(e){
        //prevent the browser from showing default error bubble/ hint
        e.preventDefault();
        // optionally fire off some custom validation handler
        // myvalidationfunction();
    };
})(), true);*/
