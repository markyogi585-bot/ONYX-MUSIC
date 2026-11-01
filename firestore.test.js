const { test, describe, before, after, beforeEach } = require('node:test');
const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds
} = require('@firebase/rules-unit-testing');
const { readFileSync } = require('node:fs');
const { doc, getDoc, setDoc, updateDoc, deleteDoc, Timestamp, setLogLevel } = require('firebase/firestore');

let testEnv;

before(async () => {
  setLogLevel('error');
  testEnv = await initializeTestEnvironment({
    projectId: 'demo-onyx-music-test',
    firestore: {
      rules: readFileSync('firestore.rules', 'utf8'),
    },
  });
});

after(async () => {
  if (testEnv) await testEnv.cleanup();
});

beforeEach(async () => {
  if (testEnv) await testEnv.clearFirestore();
});

describe('CloudMusicItem collection rules', () => {
  const validItem = {
    userId: 'alice',
    songId: 'song_dhurandhar_01',
    title: 'Dhurandhar - Title Track',
    artist: 'Hanumankind, Jasmine Sandlas',
    category: 'favorite',
    createdAt: Timestamp.now()
  };

  test('unauthenticated user cannot read or write cloud_items', async () => {
    const unauthedDb = testEnv.unauthenticatedContext().firestore();
    const docRef = doc(unauthedDb, 'users/alice/cloud_items/item_1');
    await assertFails(getDoc(docRef));
    await assertFails(setDoc(docRef, validItem));
  });

  test('authenticated owner can create, read, update, and delete valid cloud_items', async () => {
    const aliceDb = testEnv.authenticatedContext('alice').firestore();
    const docRef = doc(aliceDb, 'users/alice/cloud_items/item_1');
    await assertSucceeds(setDoc(docRef, validItem));
    await assertSucceeds(getDoc(docRef));
    await assertSucceeds(updateDoc(docRef, { title: 'Dhurandhar (Remix)' }));
    await assertSucceeds(deleteDoc(docRef));
  });

  test('non-owner cannot read, update, or delete another user cloud_items', async () => {
    await testEnv.withSecurityRulesDisabled(async (context) => {
      await setDoc(doc(context.firestore(), 'users/alice/cloud_items/item_1'), validItem);
    });

    const bobDb = testEnv.authenticatedContext('bob').firestore();
    const docRef = doc(bobDb, 'users/alice/cloud_items/item_1');
    await assertFails(getDoc(docRef));
    await assertFails(updateDoc(docRef, { title: 'Hacked' }));
    await assertFails(deleteDoc(docRef));
  });

  test('owner cannot create cloud_item with extra fields or invalid category', async () => {
    const aliceDb = testEnv.authenticatedContext('alice').firestore();
    const badExtraRef = doc(aliceDb, 'users/alice/cloud_items/item_bad1');
    await assertFails(setDoc(badExtraRef, { ...validItem, isAdmin: true }));

    const badCategoryRef = doc(aliceDb, 'users/alice/cloud_items/item_bad2');
    await assertFails(setDoc(badCategoryRef, { ...validItem, category: 'unknown_cat' }));
  });

  test('owner cannot mutate immutable userId or createdAt on update', async () => {
    await testEnv.withSecurityRulesDisabled(async (context) => {
      await setDoc(doc(context.firestore(), 'users/alice/cloud_items/item_1'), validItem);
    });

    const aliceDb = testEnv.authenticatedContext('alice').firestore();
    const docRef = doc(aliceDb, 'users/alice/cloud_items/item_1');
    await assertFails(updateDoc(docRef, { userId: 'bob' }));
  });
});
