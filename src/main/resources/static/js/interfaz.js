// Modo claro u oscuro y menú móvil. Se carga en <head> y sin defer para que la página no parpadee
// al abrirse. Sin JavaScript el menú se queda abierto, así que la navegación sigue disponible.
(function () {
  var CLAVE = 'neocamisetas.tema';
  var raiz = document.documentElement;
  raiz.classList.add('js');
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

  function abrirMenu(cabecera, abierto) {
    cabecera.classList.toggle('open', abierto);
    var boton = cabecera.querySelector('.burger');
    if (boton) {
      boton.setAttribute('aria-expanded', String(abierto));
      boton.setAttribute('aria-label', abierto ? 'Cerrar menú' : 'Abrir menú');
    }
  }

  document.addEventListener('click', function (e) {
    if (!e.target.closest) return;
    if (e.target.closest('.theme-toggle')) {
      var tema = raiz.getAttribute('data-theme') === 'dark' ? 'light' : 'dark';
      aplicar(tema);
      guardar(tema);
    }
    var cabecera = document.querySelector('.site-header.open');
    var burger = e.target.closest('.burger');
    if (burger) {
      var propia = burger.closest('.site-header');
      abrirMenu(propia, !propia.classList.contains('open'));
    } else if (cabecera && (!cabecera.contains(e.target) || e.target.closest('.menu a'))) {
      abrirMenu(cabecera, false);
    }
  });

  document.addEventListener('keydown', function (e) {
    var cabecera = document.querySelector('.site-header.open');
    if (e.key === 'Escape' && cabecera) {
      abrirMenu(cabecera, false);
      cabecera.querySelector('.burger').focus();
    }
  });
})();
