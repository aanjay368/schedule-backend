import{d as t,aj as m,j as e,p as r}from"./index-DmJHvUW7.js";import{S as p}from"./SettingDetailLayout-BQG_m5lP.js";import{P as s}from"./palette-BZAQBsFR.js";import"./InfoTip-DnVIo9rh.js";import"./info-DpPN6dKV.js";/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const h=[["rect",{width:"20",height:"14",x:"2",y:"3",rx:"2",key:"48i651"}],["line",{x1:"8",x2:"16",y1:"21",y2:"21",key:"1svkeh"}],["line",{x1:"12",x2:"12",y1:"17",y2:"21",key:"vw1qmm"}]],d=t("monitor",h);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const k=[["path",{d:"M20.985 12.486a9 9 0 1 1-9.473-9.472c.405-.022.617.46.402.803a6 6 0 0 0 8.268 8.268c.344-.215.825-.004.803.401",key:"kfwtm"}]],u=t("moon",k);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const x=[["circle",{cx:"12",cy:"12",r:"4",key:"4exip2"}],["path",{d:"M12 2v2",key:"tus03m"}],["path",{d:"M12 20v2",key:"1lh1kg"}],["path",{d:"m4.93 4.93 1.41 1.41",key:"149t6j"}],["path",{d:"m17.66 17.66 1.41 1.41",key:"ptbguv"}],["path",{d:"M2 12h2",key:"1t8f8n"}],["path",{d:"M20 12h2",key:"1q8mjw"}],["path",{d:"m6.34 17.66-1.41 1.41",key:"1m8zz5"}],["path",{d:"m19.07 4.93-1.41 1.41",key:"1shlcs"}]],y=t("sun",x),M=()=>{const{themeSetting:n,setThemeSetting:i}=m(),o=[{value:"light",label:"Mode Terang",icon:e.jsx(y,{size:18})},{value:"dark",label:"Mode Gelap",icon:e.jsx(u,{size:18})},{value:"auto",label:"Ikuti Sistem",icon:e.jsx(d,{size:18})}],l=a=>{const{value:c}=a.target;i(a.target.value),localStorage.setItem("user-theme",c)};return e.jsx(p,{title:"Tampilan",icon:s,iconColorClass:"text-pink-600",iconBgClass:"bg-purple-100 dark:bg-pink-900/30",subtitle:"Personalisasi",description:"Sesuaikan kenyamanan mata Anda saat menggunakan aplikasi di lapangan.",children:e.jsxs("div",{className:"space-y-1.5",children:[e.jsx("label",{className:"ml-1 block text-xs font-black tracking-widest text-slate-500 uppercase dark:text-slate-400",children:"Pilih Tema"}),e.jsx(r,{name:"theme",label:"Tema",icon:e.jsx(s,{size:18}),options:o,value:n,onChange:l})]})})};export{M as default};
