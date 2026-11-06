export const categories = ['Creational', 'Structural', 'Behavioral'] as const;
export const descriptions = {
  Creational: 'Control how objects come into being.',
  Structural: 'Compose objects into flexible structures.',
  Behavioral: 'Coordinate responsibilities and collaboration.',
};
export const path = (suffix = '') => `${import.meta.env.BASE_URL.replace(/\/$/, '')}/${suffix}`;
