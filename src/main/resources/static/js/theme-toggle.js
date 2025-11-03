(function(){
  const KEY='webshop_theme';
  function applyTheme(t){
    document.documentElement.setAttribute('data-theme',t);
    document.documentElement.style.colorScheme=t;
    try{localStorage.setItem(KEY,t)}catch(_){}
  }
  window.toggleTheme=function(){
    const cur=document.documentElement.getAttribute('data-theme')||'light';
    applyTheme(cur==='dark'?'light':'dark');
  };
  document.addEventListener('DOMContentLoaded',function(){
    const btn = document.getElementById('themeToggle');
    if(btn) btn.addEventListener('click', e=>{e.preventDefault(); toggleTheme();});
    const btnM = document.getElementById('themeToggleMobile');
    if(btnM) btnM.addEventListener('click', e=>{e.preventDefault(); toggleTheme();});
  });
})();