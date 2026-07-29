echo 'Installing node_modules...'
npm i

echo 'Installing dependencies...'
npm i chance luxon

echo 'Installing typescript dependencies...'
npm i -D @types/chance @types/luxon

echo 'When using Bootstrap, exclude Tailwind CSS and DaisyUI.'
echo 'Installing bootstrap...'
npm i bootstrap
npm i bootstrap-icons

echo 'Installing zustand...'
npm install zustand

echo 'Installing react-router...'
npm i react-router

#-----------------------------------------------------------------
# index.html, .prettierrc.cjs, src/App.css Update!
#-----------------------------------------------------------------
echo '=================Updating index.html...'
if [ -f "index.html" ]; then
  sed -i 's|<html lang="en">|<html lang="ko">|g' index.html
  if [ -f "index.html.bak" ]; then
    rm index.html.bak
  fi
  echo 'index.html updated successfully!'
else
  echo 'Warning: index.html not found. Skipping HTML update.'
fi

echo "=================Updating .prettierrc.cjs..."
cat << 'EOF' > .prettierrc.cjs
module.exports = {
  bracketSpacing: false,
  jsxBracketSameLine: true,
  singleQuote: true,
  trailingComma: "none",
  arrowParens: "avoid",
  semi: false,
  printWidth: 90,
};
EOF
echo ".prettierrc.cjs updated successfully!"

echo "=================Updating src/App.css..."
cat << 'EOF' > src/App.css
@import url('https://fonts.googleapis.com/icon?family=Material+Icons');

.material-icons {
  font-family: 'Material Icons';
  display: inline-block;
}

.tedurizen {
  border-radius: 10px;
  border: 1px dashed #868686;
  margin: 20px 5px;
  padding: 10px;
}

EOF
echo "src/App.css updated successfully!"

echo "=================Creating src/pages..."
mkdir -p src/pages
cat << 'EOF' > src/pages/CopyMe.tsx
import {Title} from '../components'
type DivProps = React.ComponentProps<'div'>

export default function CopyMe({className, ...props}: DivProps) {
  return (
    <div className={`mt-4 ${className} ?? ""`}>
      <Title></Title>
    </div>
  )
}
EOF
echo "src/pages/CopyMe.tsx created successfully!"

echo "=================Updating src/App.tsx..."
cat << 'EOF' > src/App.tsx
import './App.css'
import {RouterProvider} from 'react-router'
import {router} from './routes/RoutesSetup'

function App() {
  return <RouterProvider router={router} />
}
export default App

EOF
echo "src/App.tsx updated successfully!"

echo "=================Updating main.tsx..."
cat << 'EOF' > src/main.tsx
import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'

// import './assets/css/styles.css' // 외부 스타일
// import './assets/js/scripts.js' // 외부 스크립트

// npm i bootstrap 적용하면 아래의 껄로 대체 가능
import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap-icons/font/bootstrap-icons.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

import App from './App.tsx'

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <App />
  </StrictMode>
)

EOF
echo "main.tsx updated successfully!"
