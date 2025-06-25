import{c as a,r as d,b as h,u as x,C as m,F as u,j as e,L as p,O as v}from"./index-cnO6bTMh.js";import{L as b}from"./log-out-C42zfnNW.js";/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const f=[["path",{d:"M3 3v16a2 2 0 0 0 2 2h16",key:"c24i48"}],["path",{d:"M18 17V9",key:"2bz60n"}],["path",{d:"M13 17V5",key:"1frdt8"}],["path",{d:"M8 17v-3",key:"17ska0"}]],k=a("chart-column",f);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const g=[["path",{d:"m15 18-6-6 6-6",key:"1wnfg3"}]],y=a("chevron-left",g);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const j=[["path",{d:"m9 18 6-6-6-6",key:"mthhwq"}]],N=a("chevron-right",j);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const w=[["circle",{cx:"12",cy:"12",r:"10",key:"1mglay"}],["circle",{cx:"12",cy:"10",r:"3",key:"ilqhr7"}],["path",{d:"M7 20.662V19a2 2 0 0 1 2-2h6a2 2 0 0 1 2 2v1.662",key:"154egf"}]],l=a("circle-user",w);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const _=[["path",{d:"M9.671 4.136a2.34 2.34 0 0 1 4.659 0 2.34 2.34 0 0 0 3.319 1.915 2.34 2.34 0 0 1 2.33 4.033 2.34 2.34 0 0 0 0 3.831 2.34 2.34 0 0 1-2.33 4.033 2.34 2.34 0 0 0-3.319 1.915 2.34 2.34 0 0 1-4.659 0 2.34 2.34 0 0 0-3.32-1.915 2.34 2.34 0 0 1-2.33-4.033 2.34 2.34 0 0 0 0-3.831A2.34 2.34 0 0 1 6.35 6.051a2.34 2.34 0 0 0 3.319-1.915",key:"1i5ecw"}],["circle",{cx:"12",cy:"12",r:"3",key:"1v7zrd"}]],C=a("settings",_);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const L=[["path",{d:"M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2",key:"1yyitq"}],["path",{d:"M16 3.128a4 4 0 0 1 0 7.744",key:"16gr8j"}],["path",{d:"M22 21v-2a4 4 0 0 0-3-3.87",key:"kshegd"}],["circle",{cx:"9",cy:"7",r:"4",key:"nufk8"}]],M=a("users",L);function $(){const[t,o]=d.useState(!1),r=h(),{logout:n}=x(),c=[{title:"Schedule",icon:m,path:"/dev/schedule",active:r.pathname==="/dev/schedule"},{title:"Employee",icon:M,path:"/dev/employee",active:r.pathname==="/dev/employee"},{title:"Analytics",icon:k,path:"/dev/analytics",active:r.pathname==="/dev/analytics"},{title:"Reports",icon:u,path:"/dev/reports",active:r.pathname==="/dev/reports"},{title:"Settings",icon:C,path:"/dev/settings",active:r.pathname==="/dev/settings"}];return e.jsxs("aside",{className:`relative h-screen border-r border-slate-200/50 bg-gradient-to-b from-slate-50 to-slate-100 transition-all duration-300 ease-in-out dark:border-slate-700/50 dark:from-slate-900 dark:to-slate-800 ${t?"w-20":"w-64"}`,children:[e.jsx("div",{className:"border-b border-slate-200/50 p-6 dark:border-slate-700/50",children:e.jsxs("div",{className:"flex items-center justify-between",children:[!t&&e.jsxs("div",{className:"flex items-center space-x-3",children:[e.jsx("div",{className:"flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-indigo-400 to-purple-500",children:e.jsx(l,{className:"h-6 w-6 text-white"})}),e.jsxs("div",{className:"flex-1",children:[e.jsx("p",{className:"text-sm font-medium text-slate-800 dark:text-slate-100",children:"Admin User"}),e.jsx("p",{className:"text-xs text-slate-500 dark:text-slate-400",children:"Developer"})]})]}),t&&e.jsx("div",{className:"flex h-10 w-10 items-center justify-center rounded-full bg-gradient-to-br from-indigo-400 to-purple-500",children:e.jsx(l,{className:"h-6 w-6 text-white"})})]})}),e.jsx("nav",{className:"flex-1 px-3 py-4",children:e.jsx("ul",{className:"space-y-2",children:c.map(s=>{const i=s.icon;return e.jsx("li",{children:e.jsxs(p,{to:s.path,className:`group flex items-center rounded-lg px-3 py-2.5 transition-all duration-200 ease-in-out ${s.active?"border-l-4 border-blue-500 bg-gradient-to-r from-blue-500/10 to-purple-500/10 text-blue-600 dark:text-blue-400":"text-slate-600 hover:bg-slate-100 hover:text-slate-900 dark:text-slate-400 dark:hover:bg-slate-800/50 dark:hover:text-slate-100"}`,children:[e.jsx(i,{className:`h-5 w-5 flex-shrink-0 ${s.active?"text-blue-600 dark:text-blue-400":""}`}),!t&&e.jsx("span",{className:"ml-3 font-medium",children:s.title})]})},s.path)})})}),e.jsx("div",{className:"absolute right-0 bottom-0 left-0 border-t border-slate-200/50 p-4 dark:border-slate-700/50",children:e.jsxs("button",{onClick:()=>{n(),navigate("/")},className:`mt-4 flex items-center rounded-lg px-3 py-2.5 text-red-600 transition-all duration-200 ease-in-out hover:bg-red-50 dark:text-red-400 dark:hover:bg-red-900/20 ${t?"justify-center":""}`,children:[e.jsx(b,{className:"h-5 w-5 flex-shrink-0"}),!t&&e.jsx("span",{className:"ml-3 font-medium",children:"Logout"})]})}),e.jsx("button",{onClick:()=>o(!t),className:"absolute top-1/2 -right-3 flex h-6 w-6 items-center justify-center rounded-full border border-slate-200 bg-white shadow-lg transition-all duration-200 ease-in-out hover:shadow-xl dark:border-slate-700 dark:bg-slate-800",children:t?e.jsx(N,{className:"h-4 w-4 text-slate-600 dark:text-slate-400"}):e.jsx(y,{className:"h-4 w-4 text-slate-600 dark:text-slate-400"})})]})}function U(){return e.jsxs("div",{className:"flex h-screen",children:[e.jsx($,{}),e.jsx("main",{className:"flex-1 overflow-y-auto dark:bg-slate-900 bg-slate-50",children:e.jsx(v,{})})]})}export{U as default};
