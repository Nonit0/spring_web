// Modo claro u oscuro. Se carga en <head> y sin defer para que la página no parpadee al abrirse.
(function () {
  var CLAVE = 'neocamisetas.tema';
  var raiz = document.documentElement;
  var sistema = window.matchMedia ? window.matchMedia('(prefers-color-scheme: dark)') : null;

  function leer() {
    try { return localStorage.getItem(CLAVE); } catch (e) { return null; }
  }
  function guardar(tema) {
    try { localStorage.setItem(CLAVE, tema); } catch (e) { /* sin almacenamiento, dura hasta recargar */ }
  }
  function aplicar(tema) {
    raiz.setAttribute('data-theme', tema);
    var meta = document.querySelector('meta[name="theme-color"]');
    if (meta) meta.setAttribute('content', tema === 'dark' ? '#121214' : '#C6FF3D');
  }

  var guardado = leer();
  aplicar(guardado === 'dark' || guardado === 'light' ? guardado : (sistema && sistema.matches ? 'dark' : 'light'));

  // Mientras la persona no elija, sigue la preferencia del sistema.
  if (sistema && sistema.addEventListener) {
    sistema.addEventListener('change', function (e) {
      var elegido = leer();
      if (elegido !== 'dark' && elegido !== 'light') aplicar(e.matches ? 'dark' : 'light');
    });
  }

  document.addEventListener('click', function (e) {
    if (!e.target.closest || !e.target.closest('.theme-toggle')) return;
    var tema = raiz.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
    aplicar(tema);
    guardar(tema);
  });
})();
