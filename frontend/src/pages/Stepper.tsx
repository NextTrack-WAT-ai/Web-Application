import { useState } from 'react';

const Stepper = () => {
    const [currentIndex, setCurrentIndex] = useState(0);
    const sections = [
        <div>Section 1: Content for the first section</div>,
        <div>Section 2: Content for the second section</div>,
        <div>Section 3: Content for the third section</div>,
        <div>Section 4: Content for the fourth section</div>
    ]; // List of sections to display

    const handleNext = () => {
        if (currentIndex < sections.length - 1) {
            setCurrentIndex(currentIndex + 1);
        }
    };

    const handleBack = () => {
        if (currentIndex > 0) {
            setCurrentIndex(currentIndex - 1);
        }
    };

    return (
        <div>
            {sections[currentIndex]} {/* Display the current section */}
            <div>
                <button onClick={handleBack} disabled={currentIndex === 0}>
                    Back
                </button>
                <button onClick={handleNext} disabled={currentIndex === sections.length - 1}>
                    Next
                </button>
            </div>
        </div>
    );
};

export default Stepper;