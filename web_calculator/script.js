let currentDisplay = '0';
let waitingForNewOperand = false;

const displayElement = document.getElementById('display');

function updateDisplay() {
    displayElement.innerText = currentDisplay;
}

function appendNumber(number) {
    if (waitingForNewOperand) {
        currentDisplay = number;
        waitingForNewOperand = false;
    } else {
        if (currentDisplay === '0' && number !== '.') {
            currentDisplay = number;
        } else {
            // Prevent multiple decimals
            if (number === '.' && currentDisplay.includes('.')) return;
            currentDisplay += number;
        }
    }
    updateDisplay();
}

function appendOperator(operator) {
    // Prevent consecutive operators
    const lastChar = currentDisplay.slice(-1);
    if (['+', '-', '*', '/'].includes(lastChar)) {
        currentDisplay = currentDisplay.slice(0, -1) + operator;
    } else {
        currentDisplay += operator;
    }
    waitingForNewOperand = false;
    updateDisplay();
}

function clearDisplay() {
    currentDisplay = '0';
    waitingForNewOperand = false;
    updateDisplay();
}

function deleteLast() {
    if (currentDisplay.length > 1) {
        currentDisplay = currentDisplay.slice(0, -1);
    } else {
        currentDisplay = '0';
    }
    updateDisplay();
}

function calculate() {
    try {
        // Safe evaluation alternative or standard eval for simplicity in this demo
        // Replacing visual operators with JS operators if needed
        let result = eval(currentDisplay);
        
        // Handle float precision issues
        result = Math.round(result * 100000000) / 100000000;
        
        currentDisplay = String(result);
        waitingForNewOperand = true;
    } catch (error) {
        currentDisplay = 'Error';
        waitingForNewOperand = true;
    }
    updateDisplay();
}
