import{c as r,r as M,j as e,U as o}from"./index-cnO6bTMh.js";/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const F=[["path",{d:"M16 20V4a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16",key:"jecpp"}],["rect",{width:"20",height:"14",x:"2",y:"6",rx:"2",key:"i6l2r4"}]],L=r("briefcase",F);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const C=[["path",{d:"M10.733 5.076a10.744 10.744 0 0 1 11.205 6.575 1 1 0 0 1 0 .696 10.747 10.747 0 0 1-1.444 2.49",key:"ct8e1f"}],["path",{d:"M14.084 14.158a3 3 0 0 1-4.242-4.242",key:"151rxh"}],["path",{d:"M17.479 17.499a10.75 10.75 0 0 1-15.417-5.151 1 1 0 0 1 0-.696 10.75 10.75 0 0 1 4.446-5.143",key:"13bj9a"}],["path",{d:"m2 2 20 20",key:"1ooewy"}]],P=r("eye-off",C);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const $=[["path",{d:"M2.062 12.348a1 1 0 0 1 0-.696 10.75 10.75 0 0 1 19.876 0 1 1 0 0 1 0 .696 10.75 10.75 0 0 1-19.876 0",key:"1nclc0"}],["circle",{cx:"12",cy:"12",r:"3",key:"1v7zrd"}]],z=r("eye",$);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const E=[["rect",{width:"18",height:"11",x:"3",y:"11",rx:"2",ry:"2",key:"1w4ew1"}],["path",{d:"M7 11V7a5 5 0 0 1 10 0v4",key:"fwvmzm"}]],S=r("lock",E);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const V=[["path",{d:"m22 7-8.991 5.727a2 2 0 0 1-2.009 0L2 7",key:"132q7q"}],["rect",{x:"2",y:"4",width:"20",height:"16",rx:"2",key:"izxlao"}]],q=r("mail",V);/**
 * @license lucide-react v0.536.0 - ISC
 *
 * This source code is licensed under the ISC license.
 * See the LICENSE file in the root directory of this source tree.
 */const I=[["path",{d:"M20 10c0 4.993-5.539 10.193-7.399 11.799a1 1 0 0 1-1.202 0C9.539 20.193 4 14.993 4 10a8 8 0 0 1 16 0",key:"1r0f0z"}],["circle",{cx:"12",cy:"10",r:"3",key:"ilqhr7"}]],B=r("map-pin",I);function O({errorMessage:l,fields:y,onSubmit:f,onChange:n,buttonName:d}){const[x,w]=M.useState({}),b=s=>{w(t=>({...t,[s]:!t[s]}))},j=(s,t)=>t==="email"?q:t==="password"?S:s?.toLowerCase().includes("name")?o:s?.toLowerCase().includes("position")?L:s?.toLowerCase().includes("division")?B:o;return e.jsxs("div",{className:"space-y-6",children:[e.jsxs("div",{className:"text-center",children:[e.jsx("div",{className:"mx-auto flex h-12 w-12 items-center justify-center rounded-full bg-gradient-to-br from-purple-400 to-purple-600",children:e.jsx(o,{className:"h-6 w-6 text-white"})}),e.jsx("h3",{className:"mt-3 text-lg leading-6 font-medium text-gray-900 dark:text-white",children:d||"Form"})]}),e.jsxs("form",{onSubmit:f,className:"space-y-4",children:[l&&e.jsx("div",{className:"rounded-md bg-red-50 p-3",children:e.jsx("p",{className:"text-sm text-red-600",children:l})}),y.map(({name:s,label:t,errors:p,type:a="text",placeholder:k,value:m,...u})=>{const h=a==="password",N=a==="select",g=h&&x[s]?"text":a,v=j(s,a);if(N){const{inputs:i}=u;return e.jsxs("div",{className:"space-y-2",children:[e.jsx("label",{className:"block text-sm font-medium text-gray-700 dark:text-white",children:t}),p?.map(c=>e.jsx("p",{className:"text-sm text-red-600",children:c},c)),e.jsxs("select",{name:s,value:m||"",onChange:n,className:"mt-1 block w-full rounded-md border border-purple-500 px-3 py-2 shadow-sm focus:border-purple-500 focus:ring-purple-500 sm:text-sm dark:text-white",children:[e.jsxs("option",{value:"",className:"dark:text-black",children:["Select ",t]}),i.map(({value:c,label:_})=>e.jsx("option",{value:c,className:"dark:text-black",children:_},c))]})]},s)}return e.jsxs("div",{className:"space-y-1",children:[e.jsx("label",{htmlFor:s,className:"block text-sm font-medium text-gray-700 dark:text-white",children:t}),p?.map(i=>e.jsx("p",{className:"text-sm text-red-600",children:i},i)),e.jsxs("div",{className:"relative",children:[e.jsx("div",{className:"pointer-events-none absolute inset-y-0 left-0 flex items-center pl-3",children:e.jsx(v,{className:"h-5 w-5 text-gray-400"})}),e.jsx("input",{id:s,name:s,type:g,placeholder:k,onChange:n,value:m,className:"block w-full rounded-md border border-purple-300 py-2 pr-3 pl-10 shadow-sm focus:border-purple-500 focus:ring-purple-500 focus:outline-purple-500 sm:text-sm dark:text-white",...u}),h&&e.jsx("button",{type:"button",onClick:()=>b(s),className:"absolute inset-y-0 right-0 flex items-center pr-3 text-gray-400 hover:text-gray-500",children:x[s]?e.jsx(P,{className:"h-5 w-5"}):e.jsx(z,{className:"h-5 w-5"})})]})]},s)}),e.jsx("button",{type:"submit",className:"flex w-full justify-center rounded-md border border-transparent bg-purple-600 px-4 py-2 text-sm font-medium text-white shadow-sm hover:bg-purple-700 focus:ring-2 focus:ring-purple-500 focus:ring-offset-2",children:d||"Submit"})]})]})}export{L as B,O as F,B as M};
